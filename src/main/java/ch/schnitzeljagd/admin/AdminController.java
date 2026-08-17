package ch.schnitzeljagd.admin;

import ch.schnitzeljagd.common.QrGenerator;
import ch.schnitzeljagd.hunt.Hunt;
import ch.schnitzeljagd.hunt.HuntService;
import ch.schnitzeljagd.hunt.Question;
import ch.schnitzeljagd.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Der Adminbereich: Jagden anlegen und umschalten, Posten pflegen, QR-Codes
 * drucken, Teilnehmende löschen.
 * <p>
 * Alles hier ist durch die Anmeldung geschützt (siehe SecurityConfig); alle
 * verändernden Aktionen laufen über POST-Formulare, die den CSRF-Token von
 * Thymeleaf automatisch mitbekommen.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final HuntService huntService;
    private final ParticipantService participantService;
    private final QrGenerator qrGenerator;

    @Value("${schnitzeljagd.base-url}")
    private String baseUrl;

    public AdminController(HuntService huntService, ParticipantService participantService, QrGenerator qrGenerator) {
        this.huntService = huntService;
        this.participantService = participantService;
        this.qrGenerator = qrGenerator;
    }

    // ---------- Jagden ----------

    @GetMapping
    public String hunts(Model model) {
        List<Hunt> hunts = huntService.getHunts();
        Map<Long, Long> questionCounts = new LinkedHashMap<>();
        for (Hunt hunt : hunts) {
            questionCounts.put(hunt.getId(), huntService.countQuestions(hunt));
        }
        model.addAttribute("hunts", hunts);
        model.addAttribute("questionCounts", questionCounts);
        return "admin/hunts";
    }

    @PostMapping("/hunts")
    public String createHunt(@RequestParam String name, RedirectAttributes redirect) {
        try {
            Hunt hunt = huntService.createHunt(name);
            redirect.addFlashAttribute("message", "Jagd '" + hunt.getName() + "' wurde angelegt.");
            return "redirect:/admin/hunts/" + hunt.getId();
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin";
        }
    }

    @PostMapping("/hunts/{huntId}/activate")
    public String activateHunt(@PathVariable Long huntId, RedirectAttributes redirect) {
        huntService.activateHunt(huntId);
        redirect.addFlashAttribute("message", "Jagd '" + huntService.getHunt(huntId).getName() + "' ist jetzt aktiv.");
        return "redirect:/admin";
    }

    @PostMapping("/hunts/{huntId}/rename")
    public String renameHunt(@PathVariable Long huntId, @RequestParam String name, RedirectAttributes redirect) {
        try {
            huntService.renameHunt(huntId, name);
            redirect.addFlashAttribute("message", "Name geändert.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/hunts/" + huntId;
    }

    @PostMapping("/hunts/{huntId}/delete")
    public String deleteHunt(@PathVariable Long huntId, RedirectAttributes redirect) {
        String name = huntService.getHunt(huntId).getName();
        huntService.deleteHunt(huntId);
        redirect.addFlashAttribute("message", "Jagd '" + name + "' wurde gelöscht.");
        return "redirect:/admin";
    }

    // ---------- Posten ----------

    @GetMapping("/hunts/{huntId}")
    public String questions(@PathVariable Long huntId, Model model) {
        Hunt hunt = huntService.getHunt(huntId);
        model.addAttribute("hunt", hunt);
        model.addAttribute("questions", huntService.getQuestions(hunt));
        model.addAttribute("baseUrl", baseUrl);
        return "admin/questions";
    }

    @PostMapping("/hunts/{huntId}/questions")
    public String addQuestion(@PathVariable Long huntId,
                              @RequestParam String title,
                              @RequestParam String place,
                              @RequestParam String text,
                              @RequestParam(required = false) String hint,
                              @RequestParam String answers,
                              RedirectAttributes redirect) {
        try {
            huntService.addQuestion(huntId, title, place, text, hint, answers);
            redirect.addFlashAttribute("message", "Posten '" + title + "' wurde hinzugefügt.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/hunts/" + huntId;
    }

    @GetMapping("/questions/{questionId}")
    public String editQuestion(@PathVariable Long questionId, Model model) {
        Question question = huntService.getQuestion(questionId);
        model.addAttribute("question", question);
        model.addAttribute("hunt", question.getHunt());
        model.addAttribute("baseUrl", baseUrl);
        return "admin/question-edit";
    }

    @PostMapping("/questions/{questionId}")
    public String updateQuestion(@PathVariable Long questionId,
                                 @RequestParam String title,
                                 @RequestParam String place,
                                 @RequestParam String text,
                                 @RequestParam(required = false) String hint,
                                 @RequestParam String answers,
                                 RedirectAttributes redirect) {
        Long huntId = huntService.getQuestion(questionId).getHunt().getId();
        try {
            huntService.updateQuestion(questionId, title, place, text, hint, answers);
            redirect.addFlashAttribute("message", "Posten wurde gespeichert.");
            return "redirect:/admin/hunts/" + huntId;
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/questions/" + questionId;
        }
    }

    @PostMapping("/questions/{questionId}/delete")
    public String deleteQuestion(@PathVariable Long questionId, RedirectAttributes redirect) {
        Long huntId = huntService.getQuestion(questionId).getHunt().getId();
        huntService.deleteQuestion(questionId);
        redirect.addFlashAttribute("message", "Posten wurde gelöscht.");
        return "redirect:/admin/hunts/" + huntId;
    }

    @PostMapping("/questions/{questionId}/move")
    public String moveQuestion(@PathVariable Long questionId, @RequestParam int delta) {
        Long huntId = huntService.getQuestion(questionId).getHunt().getId();
        huntService.moveQuestion(questionId, delta);
        return "redirect:/admin/hunts/" + huntId;
    }

    // ---------- QR-Codes ----------

    /** Druckansicht: alle Posten einer Jagd als QR-Codes, je einer pro Seite. */
    @GetMapping("/hunts/{huntId}/print")
    public String printQrCodes(@PathVariable Long huntId, Model model) {
        Hunt hunt = huntService.getHunt(huntId);
        model.addAttribute("hunt", hunt);
        model.addAttribute("questions", huntService.getQuestions(hunt));
        model.addAttribute("baseUrl", baseUrl);
        return "admin/print";
    }

    @GetMapping("/qr/{token}.png")
    public ResponseEntity<byte[]> qrImage(@PathVariable String token,
                                          @RequestParam(defaultValue = "400") int size) {
        return pngResponse(qrGenerator.toPngBytes(questionUrl(token), size));
    }

    /**
     * QR-Code der Startseite — damit auch die Anmeldung gescannt statt getippt wird.
     * Nebeneffekt: Wer sich per Scan anmeldet, landet im selben Browser, in dem er
     * spaeter die Posten scannt. Das gemerkte Codecookie greift dann zuverlaessig.
     */
    @GetMapping("/qr-start.png")
    public ResponseEntity<byte[]> startQrImage(@RequestParam(defaultValue = "400") int size) {
        return pngResponse(qrGenerator.toPngBytes(normalizedBaseUrl() + "/", size));
    }

    private ResponseEntity<byte[]> pngResponse(byte[] png) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(png);
    }

    // ---------- Teilnehmende ----------

    @GetMapping("/participants")
    public String participants(Model model) {
        model.addAttribute("participants", participantService.getParticipants());
        return "admin/participants";
    }

    @PostMapping("/participants/delete-all")
    public String deleteAllParticipants(RedirectAttributes redirect) {
        participantService.deleteAll();
        redirect.addFlashAttribute("message", "Alle Teilnehmenden wurden gelöscht.");
        return "redirect:/admin/participants";
    }

    @PostMapping("/participants/delete")
    public String deleteParticipant(@RequestParam String code, RedirectAttributes redirect) {
        participantService.deleteByCode(code);
        redirect.addFlashAttribute("message", "Teilnehmer:in mit Code " + code + " wurde gelöscht.");
        return "redirect:/admin/participants";
    }

    /** Die vollständige URL, die im QR-Code eines Postens steckt. */
    private String questionUrl(String token) {
        return normalizedBaseUrl() + "/q/" + token;
    }

    private String normalizedBaseUrl() {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
