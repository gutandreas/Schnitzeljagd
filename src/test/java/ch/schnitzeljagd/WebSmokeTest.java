package ch.schnitzeljagd;

import ch.schnitzeljagd.hunt.Hunt;
import ch.schnitzeljagd.hunt.HuntService;
import ch.schnitzeljagd.hunt.Question;
import ch.schnitzeljagd.participant.Participant;
import ch.schnitzeljagd.participant.ParticipantService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Rendert jede Seite einmal durch. Fehler in Thymeleaf-Ausdruecken zeigen sich
 * erst zur Laufzeit — dieser Test faengt sie ab, ohne dass ein Server laufen muss.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HuntService huntService;

    @Autowired
    private ParticipantService participantService;

    private Hunt hunt;
    private Question question;
    private Participant participant;

    @BeforeEach
    void setUp() {
        hunt = huntService.createHunt("Smoke-Test-Jagd");
        huntService.addQuestion(hunt.getId(), "Einstieg", "Zimmer 1", "Welches Fach?", "Beginnt mit I", "Informatik");
        // Ein zweiter Posten, damit die Antwort auf den naechsten Ort verweisen kann.
        huntService.addQuestion(hunt.getId(), "Zweiter", "Lichthof", "Eine Zahl?", null, "42");
        huntService.activateHunt(hunt.getId());
        question = huntService.getQuestions(hunt).get(0);
        participant = participantService.register("Anna", "Muster");
    }

    // ---------- oeffentliche Seiten ----------

    @Test
    void startseiteWirdGerendert() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Smoke-Test-Jagd")));
    }

    @Test
    void anmeldungLegtTeilnehmerAnUndLeitetWeiter() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("firstName", "Beat")
                        .param("lastName", "Beispiel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/welcome"));
    }

    @Test
    void willkommensseiteZeigtDenCode() throws Exception {
        mockMvc.perform(get("/welcome").cookie(new Cookie("sjcode", participant.getCode())))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(participant.getCode())));
    }

    @Test
    void startLeitetZumErstenPosten() throws Exception {
        mockMvc.perform(get("/start").cookie(new Cookie("sjcode", participant.getCode())))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void postenseiteWirdGerendert() throws Exception {
        mockMvc.perform(get("/q/" + question.getToken()).cookie(new Cookie("sjcode", participant.getCode())))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Welches Fach?")));
    }

    @Test
    void unbekannterPostenZeigtHinweisStattFehler() throws Exception {
        mockMvc.perform(get("/q/GIBTSNICHT"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Hoppla")));
    }

    @Test
    void antwortWirdVerarbeitet() throws Exception {
        mockMvc.perform(post("/q/" + question.getToken() + "/answer").with(csrf())
                        .param("code", participant.getCode())
                        .param("answer", "Informatik"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("richtig")));
    }

    /** Der Tipptext darf erst in der Seite auftauchen, wenn er angefordert wurde. */
    @Test
    void tippStehtErstNachDemAnfordernInDerSeite() throws Exception {
        mockMvc.perform(get("/q/" + question.getToken()).cookie(new Cookie("sjcode", participant.getCode())))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Beginnt mit I"))));

        mockMvc.perform(post("/q/" + question.getToken() + "/hint").with(csrf())
                        .param("code", participant.getCode()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Beginnt mit I")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("eine Minute")));
    }

    /** Der Timer-Startwert im HTML muss sofort den Tippzuschlag enthalten. */
    @Test
    void timerZeigtDenTippzuschlagOhneErneutesLaden() throws Exception {
        String vorher = mockMvc.perform(get("/q/" + question.getToken()).cookie(new Cookie("sjcode", participant.getCode())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long sekundenVorher = extractTimerSeconds(vorher);

        String nachher = mockMvc.perform(post("/q/" + question.getToken() + "/hint").with(csrf())
                        .param("code", participant.getCode()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long sekundenNachher = extractTimerSeconds(nachher);

        org.junit.jupiter.api.Assertions.assertTrue(sekundenNachher >= sekundenVorher + 60,
                "Zuschlag fehlt im Timer: vorher " + sekundenVorher + "s, nachher " + sekundenNachher + "s");
    }

    private long extractTimerSeconds(String html) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("data-seconds=\"(\\d+)\"").matcher(html);
        org.junit.jupiter.api.Assertions.assertTrue(matcher.find(), "Kein Timer-Attribut im HTML gefunden");
        return Long.parseLong(matcher.group(1));
    }

    @Test
    void abschlussseiteWirdGerendert() throws Exception {
        mockMvc.perform(get("/checkout").cookie(new Cookie("sjcode", participant.getCode())))
                .andExpect(status().isOk());
        mockMvc.perform(post("/checkout").with(csrf()).param("code", participant.getCode()))
                .andExpect(status().isOk());
    }

    @Test
    void ranglisteWirdGerendert() throws Exception {
        mockMvc.perform(get("/ranking")).andExpect(status().isOk());
    }

    @Test
    void anmeldemaskeWirdGerendert() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk());
    }

    // ---------- Adminbereich ----------

    @Test
    void adminIstOhneAnmeldungGesperrt() throws Exception {
        mockMvc.perform(get("/admin")).andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/admin/participants/delete-all").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminseitenWerdenGerendert() throws Exception {
        mockMvc.perform(get("/admin")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/hunts/" + hunt.getId())).andExpect(status().isOk());
        mockMvc.perform(get("/admin/questions/" + question.getId())).andExpect(status().isOk());
        mockMvc.perform(get("/admin/hunts/" + hunt.getId() + "/print")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/participants")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void qrBilderWerdenErzeugt() throws Exception {
        mockMvc.perform(get("/admin/qr/" + question.getToken() + ".png"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
        mockMvc.perform(get("/admin/qr-start.png"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postenAnlegenUndLoeschen() throws Exception {
        mockMvc.perform(post("/admin/hunts/" + hunt.getId() + "/questions").with(csrf())
                        .param("title", "Neuer Posten")
                        .param("place", "Lichthof")
                        .param("text", "Eine Frage?")
                        .param("answers", "ja; jawohl"))
                .andExpect(status().is3xxRedirection());

        Question added = huntService.getQuestions(hunt).stream()
                .filter(q -> q.getTitle().equals("Neuer Posten"))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/admin/questions/" + added.getId() + "/delete").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
