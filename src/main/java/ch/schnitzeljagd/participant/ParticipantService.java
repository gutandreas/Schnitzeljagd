package ch.schnitzeljagd.participant;

import ch.schnitzeljagd.common.RandomCodes;
import ch.schnitzeljagd.hunt.Hunt;
import ch.schnitzeljagd.hunt.HuntService;
import ch.schnitzeljagd.hunt.Question;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Der Ablauf der Jagd: anmelden, Antworten prüfen, abschliessen.
 */
@Service
public class ParticipantService {

    private static final int CODE_LENGTH = 4;
    private static final int MAX_CODE_ATTEMPTS = 50;
    private static final int MIN_MEMBERS = 1;
    private static final int MAX_MEMBERS = 5;

    private final ParticipantRepository participantRepository;
    private final HuntService huntService;

    /** true = jede Person bekommt eine eigene, gemischte Reihenfolge (verhindert Stau an den Posten). */
    @Value("${schnitzeljagd.shuffle:true}")
    private boolean shuffle;

    public ParticipantService(ParticipantRepository participantRepository, HuntService huntService) {
        this.participantRepository = participantRepository;
        this.huntService = huntService;
    }

    /** Das Ergebnis einer Aktion, wie es der teilnehmenden Person angezeigt wird. */
    public record GameResult(boolean success, String message) {
    }

    @Transactional
    public Participant register(String groupName, List<String> memberNames) {
        Hunt hunt = huntService.getActiveHunt()
                .orElseThrow(() -> new IllegalStateException("Zurzeit ist keine Schnitzeljagd freigeschaltet."));

        List<Question> questions = huntService.getQuestions(hunt);
        if (questions.isEmpty()) {
            throw new IllegalStateException("Die Schnitzeljagd hat noch keine Posten.");
        }

        Participant participant = new Participant(
                generateCode(),
                requireText(groupName, "Bitte gib einen Gruppennamen ein."),
                cleanMemberNames(memberNames),
                hunt,
                buildRoute(questions));

        return participantRepository.save(participant);
    }

    /**
     * Entfernt leere Einträge (nicht ausgefüllte, aber mitgeschickte Namensfelder)
     * und prüft die Anzahl. Serverseitig, weil sich das clientseitige Limit von
     * fünf Feldern mit einem manuellen POST umgehen liesse.
     */
    private List<String> cleanMemberNames(List<String> memberNames) {
        List<String> cleaned = memberNames == null
                ? List.of()
                : memberNames.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(name -> !name.isEmpty())
                        .toList();

        if (cleaned.size() < MIN_MEMBERS) {
            throw new IllegalArgumentException("Gib mindestens einen Namen ein.");
        }
        if (cleaned.size() > MAX_MEMBERS) {
            throw new IllegalArgumentException("Höchstens " + MAX_MEMBERS + " Namen sind erlaubt.");
        }
        return cleaned;
    }

    @Transactional(readOnly = true)
    public Optional<Participant> findByCode(String code) {
        return participantRepository.findByCode(normalizeCode(code));
    }

    /**
     * Prüft, ob der gescannte Posten der aktuelle der Person ist — ohne eine
     * Antwort zu verlangen. Wird schon beim Laden der Seite aufgerufen, damit
     * der Hinweis auf den richtigen Posten sofort beim Scannen erscheint statt
     * erst beim Abschicken einer Antwort, und die falsche Frage gar nicht erst
     * zu sehen ist.
     *
     * @return leer, wenn der Posten stimmt (die Frage darf gezeigt werden);
     *         sonst eine Rückmeldung mit dem Hinweis, wo der richtige Posten ist.
     *         Auch leer, wenn Code oder Posten unbekannt sind — das behandeln
     *         die Aufrufer bereits selbst.
     */
    @Transactional(readOnly = true)
    public Optional<GameResult> checkPosition(String code, String token) {
        Optional<Participant> participant = participantRepository.findByCode(normalizeCode(code));
        Optional<Question> scanned = huntService.findByToken(token);
        if (participant.isEmpty() || scanned.isEmpty()) {
            return Optional.empty();
        }
        return checkPosition(participant.get(), scanned.get());
    }

    private Optional<GameResult> checkPosition(Participant participant, Question scanned) {
        if (participant.hasSolvedEverything()) {
            return Optional.of(new GameResult(true,
                    "Du hast bereits alle Posten gelöst! Suche den Ziel-QR-Code, um abzuschliessen."));
        }

        Long currentId = participant.getCurrentQuestionId();
        if (!scanned.getId().equals(currentId)) {
            Question current = huntService.getQuestion(currentId);
            return Optional.of(new GameResult(false,
                    "Das ist nicht dein Posten! Deiner befindet sich hier: " + current.getPlace()));
        }

        return Optional.empty();
    }

    /**
     * Prüft die Antwort an einem Posten. Der Posten muss der aktuelle der Person
     * sein — sonst wird gesagt, wo der richtige steht.
     */
    @Transactional
    public GameResult checkAnswer(String code, String token, String answer) {
        Optional<Participant> found = participantRepository.findByCode(normalizeCode(code));
        if (found.isEmpty()) {
            return new GameResult(false, "Diesen Code kennen wir nicht. Hast du dich vertippt?");
        }
        Participant participant = found.get();

        Optional<Question> scanned = huntService.findByToken(token);
        if (scanned.isEmpty()) {
            return new GameResult(false, "Diesen Posten gibt es nicht.");
        }

        Optional<GameResult> positionProblem = checkPosition(participant, scanned.get());
        if (positionProblem.isPresent()) {
            return positionProblem.get();
        }

        if (!scanned.get().accepts(answer)) {
            return new GameResult(false, "Die Antwort ist nicht korrekt...");
        }

        Long nextId = participant.getNextQuestionId();
        participant.markSolved();

        if (nextId == null) {
            return new GameResult(true, "Richtig — und damit hast du alle Posten gelöst! Suche den Ziel-QR-Code, um abzuschliessen.");
        }
        Question next = huntService.getQuestion(nextId);
        return new GameResult(true, "Die Antwort ist richtig! Den nächsten Posten findest du hier: " + next.getPlace());
    }

    /**
     * Schaltet den Tipp zum aktuellen Posten frei. Der erste Bezug kostet eine
     * Minute; ein erneuter Aufruf (Neuladen der Seite) kostet nichts mehr.
     * <p>
     * Der Tipp wird bewusst erst hier geliefert und steht nicht schon versteckt
     * in der Seite — sonst wäre er über den Seitenquelltext gratis zu haben.
     */
    @Transactional
    public GameResult requestHint(String code, String token) {
        Optional<Participant> found = participantRepository.findByCode(normalizeCode(code));
        if (found.isEmpty()) {
            return new GameResult(false, "Für einen Tipp brauchen wir deinen Code.");
        }
        Participant participant = found.get();

        Optional<Question> scanned = huntService.findByToken(token);
        if (scanned.isEmpty()) {
            return new GameResult(false, "Diesen Posten gibt es nicht.");
        }
        Question question = scanned.get();

        if (question.getHint() == null) {
            return new GameResult(false, "Zu diesem Posten gibt es keinen Tipp.");
        }

        Long currentId = participant.getCurrentQuestionId();
        if (currentId == null || !question.getId().equals(currentId)) {
            return new GameResult(false, "Das ist nicht dein aktueller Posten — hier gibt es keinen Tipp.");
        }

        if (!participant.requestHint(question.getId())) {
            return new GameResult(true, "Diesen Tipp hattest du schon — er kostet dich kein zweites Mal.");
        }
        return new GameResult(true, "Tipp freigeschaltet. Dafür kommt eine Minute auf deine Zeit.");
    }

    /** Schliesst die Jagd ab und stoppt die Zeit. */
    @Transactional
    public GameResult finish(String code) {
        Optional<Participant> found = participantRepository.findByCode(normalizeCode(code));
        if (found.isEmpty()) {
            return new GameResult(false, "Diesen Code kennen wir nicht. Hast du dich vertippt?");
        }
        Participant participant = found.get();

        if (!participant.hasSolvedEverything()) {
            int offen = participant.getTotal() - participant.getSolved();
            return new GameResult(false, "Du hast noch nicht alle Posten gelöst — es fehlen noch " + offen + ".");
        }

        boolean alreadyFinished = participant.isFinished();
        participant.finish();
        String prefix = alreadyFinished ? "Du hast bereits abgeschlossen, " : "Herzliche Gratulation, ";

        int hints = participant.getHintCount();
        String zuschlag = hints == 0
                ? ""
                : " (darin " + hints + (hints == 1 ? " Minute" : " Minuten") + " Zuschlag für "
                        + (hints == 1 ? "einen Tipp" : hints + " Tipps") + ")";

        return new GameResult(true, prefix + participant.getGroupName() + ", du hast die Schnitzeljagd in "
                + participant.getDurationAsFormattedString() + " gemeistert!" + zuschlag);
    }

    @Transactional(readOnly = true)
    public List<Participant> getRanking() {
        return huntService.getActiveHunt()
                .map(participantRepository::findRankingByHunt)
                .orElseGet(List::of);
    }

    @Transactional(readOnly = true)
    public List<Participant> getParticipants() {
        return participantRepository.findAllByOrderByStartDesc();
    }

    @Transactional
    public void deleteByCode(String code) {
        participantRepository.findByCode(normalizeCode(code)).ifPresent(participantRepository::delete);
    }

    @Transactional
    public void deleteAll() {
        participantRepository.deleteAll();
    }

    /**
     * Die persönliche Postenreihenfolge. Posten 1 bleibt immer vorne — er ist der
     * Einstieg und wird gemeinsam gestartet; gemischt wird nur der Rest.
     */
    private List<Long> buildRoute(List<Question> questions) {
        List<Long> route = new ArrayList<>(questions.stream().map(Question::getId).toList());
        if (shuffle && route.size() > 2) {
            List<Long> rest = route.subList(1, route.size());
            Collections.shuffle(rest);
        }
        return route;
    }

    /**
     * Sucht einen freien Code. Die Eindeutigkeit garantiert am Ende die
     * Datenbank (unique-Spalte); diese Schleife hält nur die Kollisionen klein.
     */
    private String generateCode() {
        for (int attempt = 0; attempt < MAX_CODE_ATTEMPTS; attempt++) {
            String code = RandomCodes.generate(CODE_LENGTH);
            if (!participantRepository.existsByCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Es liess sich kein freier Code finden.");
    }

    /** Codes werden gross geschrieben — auf dem Handy tippt sich sonst leicht Kleinschrift ein. */
    private String normalizeCode(String code) {
        return code == null ? "" : code.trim().toUpperCase();
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
