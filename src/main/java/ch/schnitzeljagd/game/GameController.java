package ch.schnitzeljagd.game;

import ch.schnitzeljagd.hunt.Hunt;
import ch.schnitzeljagd.hunt.HuntService;
import ch.schnitzeljagd.hunt.Question;
import ch.schnitzeljagd.participant.Participant;
import ch.schnitzeljagd.participant.ParticipantService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Alles, was die Teilnehmenden sehen: anmelden, Posten scannen, antworten,
 * abschliessen, Rangliste.
 * <p>
 * Alle verändernden Aktionen sind POST. Früher liefen sie über GET — ein
 * Neuladen der Seite oder ein Vorschau-Scanner konnte damit den nächsten
 * Posten freischalten oder die Zeit stoppen.
 */
@Controller
public class GameController {

    /** Der Code der teilnehmenden Person, damit er nicht an jedem Posten neu getippt werden muss. */
    private static final String CODE_COOKIE = "sjcode";
    private static final int CODE_COOKIE_MAX_AGE_SECONDS = 8 * 60 * 60;

    private final HuntService huntService;
    private final ParticipantService participantService;

    @Value("${schnitzeljagd.title}")
    private String title;

    public GameController(HuntService huntService, ParticipantService participantService) {
        this.huntService = huntService;
        this.participantService = participantService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", title);
        model.addAttribute("hunt", huntService.getActiveHunt().orElse(null));
        return "index";
    }

    @PostMapping("/register")
    public String register(@RequestParam String groupName,
                           @RequestParam(required = false) List<String> members,
                           HttpServletResponse response,
                           Model model) {
        try {
            Participant participant = participantService.register(groupName, members);
            writeCodeCookie(response, participant.getCode());
            return "redirect:/welcome";
        } catch (RuntimeException e) {
            model.addAttribute("title", title);
            model.addAttribute("hunt", huntService.getActiveHunt().orElse(null));
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }

    @GetMapping("/welcome")
    public String welcome(HttpServletRequest request, Model model) {
        Optional<Participant> participant = participantService.findByCode(readCodeCookie(request));
        if (participant.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("participant", participant.get());
        return "welcome";
    }

    /** Springt zum ersten Posten der persönlichen Reihenfolge. */
    @GetMapping("/start")
    public String start(HttpServletRequest request) {
        Optional<Participant> participant = participantService.findByCode(readCodeCookie(request));
        if (participant.isEmpty()) {
            return "redirect:/";
        }
        Long questionId = participant.get().getCurrentQuestionId();
        if (questionId == null) {
            return "redirect:/checkout";
        }
        return "redirect:/q/" + huntService.getQuestion(questionId).getToken();
    }

    /**
     * Wird vom QR-Code am Posten aufgerufen. Ist der Code aus dem Cookie bekannt
     * und der Posten nicht der aktuelle der Person, wird das schon hier gesagt —
     * die falsche Frage wird gar nicht erst gezeigt, statt den Fehler erst nach
     * dem Abschicken einer Antwort zu melden.
     */
    @GetMapping("/q/{token}")
    public String showQuestion(@PathVariable String token, HttpServletRequest request, Model model) {
        Optional<Question> question = huntService.findByToken(token);
        if (question.isEmpty()) {
            model.addAttribute("message", "Diesen Posten gibt es nicht. Stimmt der QR-Code?");
            return "unknown-post";
        }

        String code = readCodeCookie(request);
        Optional<ParticipantService.GameResult> positionProblem = participantService.checkPosition(code, token);
        if (positionProblem.isPresent()) {
            model.addAttribute("message", positionProblem.get().message());
            return "wrong-post";
        }

        addQuestionAttributes(model, question.get(), code, null, false);
        return "questions";
    }

    @PostMapping("/q/{token}/answer")
    public String answer(@PathVariable String token,
                         @RequestParam String code,
                         @RequestParam String answer,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         Model model) {
        Optional<Question> question = huntService.findByToken(token);
        if (question.isEmpty()) {
            model.addAttribute("message", "Diesen Posten gibt es nicht. Stimmt der QR-Code?");
            return "unknown-post";
        }

        ParticipantService.GameResult result = participantService.checkAnswer(code, token, answer);
        if (result.success()) {
            // Der zuletzt benutzte Code wird gemerkt — falls jemand das Gerät gewechselt hat.
            writeCodeCookie(response, code.trim().toUpperCase());
        }

        // Nur eine richtige Antwort blendet Frage und Formular aus — ein
        // erfolgreich angeforderter Tipp (unten) tut das ausdruecklich nicht.
        addQuestionAttributes(model, question.get(), code, result, result.success());
        return "questions";
    }

    /** Der Tipp kostet eine Minute — deshalb ist auch das ein POST und kein Link. */
    @PostMapping("/q/{token}/hint")
    public String hint(@PathVariable String token,
                       @RequestParam String code,
                       HttpServletResponse response,
                       Model model) {
        Optional<Question> question = huntService.findByToken(token);
        if (question.isEmpty()) {
            model.addAttribute("message", "Diesen Posten gibt es nicht. Stimmt der QR-Code?");
            return "unknown-post";
        }

        ParticipantService.GameResult result = participantService.requestHint(code, token);
        if (result.success()) {
            writeCodeCookie(response, code.trim().toUpperCase());
        }

        addQuestionAttributes(model, question.get(), code, result, false);
        return "questions";
    }

    @GetMapping("/checkout")
    public String checkout(HttpServletRequest request, Model model) {
        String code = readCodeCookie(request);
        model.addAttribute("code", code);
        model.addAttribute("participant", participantService.findByCode(code).orElse(null));
        return "checkout";
    }

    @PostMapping("/checkout")
    public String finish(@RequestParam String code, Model model) {
        model.addAttribute("code", code);
        model.addAttribute("participant", participantService.findByCode(code).orElse(null));
        model.addAttribute("result", participantService.finish(code));
        return "checkout";
    }

    @GetMapping("/ranking")
    public String ranking(Model model) {
        model.addAttribute("title", title);
        model.addAttribute("hunt", huntService.getActiveHunt().orElse(null));
        model.addAttribute("ranking", participantService.getRanking());
        return "ranking";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** Füllt die Fragenseite — inklusive Fortschritt, sofern der Code bekannt ist. */
    private void addQuestionAttributes(Model model, Question question, String code,
                                       ParticipantService.GameResult result, boolean answeredCorrectly) {
        Hunt hunt = question.getHunt();
        model.addAttribute("question", question);
        model.addAttribute("code", code == null ? "" : code);
        model.addAttribute("result", result);
        // Steuert, ob Frage und Antwortformular durch ein "Bravo!" ersetzt werden.
        model.addAttribute("answeredCorrectly", answeredCorrectly);

        Optional<Participant> participant = participantService.findByCode(code);
        model.addAttribute("participant", participant.orElse(null));
        model.addAttribute("total", huntService.countQuestions(hunt));
        model.addAttribute("everythingSolved", participant.map(Participant::hasSolvedEverything).orElse(false));

        // Startwert fuer den Client-Timer: verstrichene Zeit inklusive Tippzuschlaege,
        // zum Renderzeitpunkt berechnet. Der Client zaehlt danach selbst weiter, ohne
        // den Server erneut zu fragen; ein neuer Wert kommt erst mit dem naechsten
        // vollen Seitenaufbau (Antwort senden, Tipp anfordern, Posten wechseln).
        model.addAttribute("elapsedSeconds", participant.map(p -> p.getElapsedSeconds(LocalDateTime.now())).orElse(null));
        model.addAttribute("timerRunning", participant.map(p -> !p.isFinished()).orElse(false));

        // Der Tipptext geht nur an die Seite, wenn er auch bezahlt wurde — sonst
        // liesse er sich im Seitenquelltext gratis nachlesen.
        boolean hintUnlocked = participant
                .map(p -> p.hasRequestedHint(question.getId()))
                .orElse(false);
        model.addAttribute("hintUnlocked", hintUnlocked);
        model.addAttribute("hintText", hintUnlocked ? question.getHint() : null);
        model.addAttribute("hintAvailable", question.getHint() != null);
    }

    private void writeCodeCookie(HttpServletResponse response, String code) {
        Cookie cookie = new Cookie(CODE_COOKIE, code);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(CODE_COOKIE_MAX_AGE_SECONDS);
        response.addCookie(cookie);
    }

    private String readCodeCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return "";
        }
        for (Cookie cookie : request.getCookies()) {
            if (CODE_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return "";
    }
}
