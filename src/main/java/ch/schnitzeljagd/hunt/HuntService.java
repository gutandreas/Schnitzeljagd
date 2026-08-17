package ch.schnitzeljagd.hunt;

import ch.schnitzeljagd.common.RandomCodes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Verwaltet Jagden und ihre Posten. Genau eine Jagd ist aktiv — sie bestimmt,
 * welche Fragen die Teilnehmenden bekommen.
 */
@Service
public class HuntService {

    private static final int TOKEN_LENGTH = 6;

    private final HuntRepository huntRepository;
    private final QuestionRepository questionRepository;

    public HuntService(HuntRepository huntRepository, QuestionRepository questionRepository) {
        this.huntRepository = huntRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    public List<Hunt> getHunts() {
        return huntRepository.findAllByOrderByCreatedAsc();
    }

    @Transactional(readOnly = true)
    public Optional<Hunt> getActiveHunt() {
        return huntRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Hunt getHunt(Long huntId) {
        return huntRepository.findById(huntId)
                .orElseThrow(() -> new IllegalArgumentException("Jagd " + huntId + " gibt es nicht."));
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestions(Hunt hunt) {
        return questionRepository.findByHuntOrderByPositionAsc(hunt);
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestions(Long huntId) {
        return getQuestions(getHunt(huntId));
    }

    @Transactional(readOnly = true)
    public long countQuestions(Hunt hunt) {
        return questionRepository.countByHunt(hunt);
    }

    @Transactional(readOnly = true)
    public Optional<Question> findByToken(String token) {
        return questionRepository.findByToken(token);
    }

    @Transactional(readOnly = true)
    public Question getQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Posten " + questionId + " gibt es nicht."));
    }

    /**
     * Löst eine Liste von Posten-IDs (z.B. die persönliche Route einer Person)
     * zu den zugehörigen Fragen auf — in genau der übergebenen Reihenfolge, für
     * die Admin-Ansicht der Teilnehmerliste.
     * <p>
     * {@code findAllById} liefert die Treffer in beliebiger Reihenfolge, deshalb
     * der Umweg über eine Map. Ein inzwischen gelöschter Posten fällt dabei
     * kommentarlos raus, statt eine NullPointerException auszulösen.
     */
    @Transactional(readOnly = true)
    public List<Question> getQuestionsByIds(List<Long> ids) {
        Map<Long, Question> byId = questionRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Question::getId, q -> q));
        return ids.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional
    public Hunt createHunt(String name) {
        return huntRepository.save(new Hunt(requireText(name, "Die Jagd braucht einen Namen.")));
    }

    @Transactional
    public void renameHunt(Long huntId, String name) {
        getHunt(huntId).setName(requireText(name, "Die Jagd braucht einen Namen."));
    }

    /** Setzt genau eine Jagd aktiv und alle anderen inaktiv. */
    @Transactional
    public void activateHunt(Long huntId) {
        Hunt target = getHunt(huntId);
        for (Hunt hunt : huntRepository.findAll()) {
            hunt.setActive(hunt.getId().equals(target.getId()));
        }
    }

    @Transactional
    public void deleteHunt(Long huntId) {
        Hunt hunt = getHunt(huntId);
        questionRepository.deleteByHunt(hunt);
        huntRepository.delete(hunt);
    }

    @Transactional
    public Question addQuestion(Long huntId, String title, String place, String text, String hint, String answerLine) {
        Hunt hunt = getHunt(huntId);
        int position = (int) questionRepository.countByHunt(hunt) + 1;
        Question question = new Question(
                hunt,
                position,
                generateToken(),
                requireText(title, "Der Posten braucht einen Titel."),
                requireText(place, "Der Posten braucht einen Ort."),
                requireText(text, "Der Posten braucht eine Frage."),
                trimToNull(hint),
                parseAnswers(answerLine));
        return questionRepository.save(question);
    }

    @Transactional
    public void updateQuestion(Long questionId, String title, String place, String text, String hint, String answerLine) {
        Question question = getQuestion(questionId);
        question.setTitle(requireText(title, "Der Posten braucht einen Titel."));
        question.setPlace(requireText(place, "Der Posten braucht einen Ort."));
        question.setText(requireText(text, "Der Posten braucht eine Frage."));
        question.setHint(trimToNull(hint));
        question.setAnswers(parseAnswers(answerLine));
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = getQuestion(questionId);
        Hunt hunt = question.getHunt();
        questionRepository.delete(question);
        questionRepository.flush();
        renumber(hunt);
    }

    /** Verschiebt einen Posten in der Reihenfolge; {@code delta} ist -1 (hoch) oder +1 (runter). */
    @Transactional
    public void moveQuestion(Long questionId, int delta) {
        Question question = getQuestion(questionId);
        List<Question> questions = getQuestions(question.getHunt());
        int index = -1;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId().equals(questionId)) {
                index = i;
                break;
            }
        }
        int target = index + delta;
        if (index < 0 || target < 0 || target >= questions.size()) {
            return;
        }
        Question other = questions.get(target);
        int temp = question.getPosition();
        question.setPosition(other.getPosition());
        other.setPosition(temp);
    }

    /** Erzeugt einen Token, der noch nicht vergeben ist. */
    private String generateToken() {
        String token;
        do {
            token = RandomCodes.generate(TOKEN_LENGTH);
        } while (questionRepository.existsByToken(token));
        return token;
    }

    /** Schliesst Lücken in der Reihenfolge, damit die Posten wieder 1..n durchlaufen. */
    private void renumber(Hunt hunt) {
        List<Question> questions = getQuestions(hunt);
        for (int i = 0; i < questions.size(); i++) {
            questions.get(i).setPosition(i + 1);
        }
    }

    /**
     * Zerlegt die Eingabezeile des Adminformulars in einzelne Antworten.
     * Getrennt wird mit Semikolon oder Zeilenumbruch, damit Antworten selbst
     * ein Komma enthalten dürfen.
     */
    private List<String> parseAnswers(String answerLine) {
        if (answerLine == null || answerLine.isBlank()) {
            throw new IllegalArgumentException("Der Posten braucht mindestens eine richtige Antwort.");
        }
        List<String> answers = new ArrayList<>(Arrays.stream(answerLine.split("[;\\r\\n]"))
                .map(String::trim)
                .filter(a -> !a.isEmpty())
                .toList());
        if (answers.isEmpty()) {
            throw new IllegalArgumentException("Der Posten braucht mindestens eine richtige Antwort.");
        }
        return answers;
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
