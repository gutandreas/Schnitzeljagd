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
        participant = participantService.register("Die Testgruppe", java.util.List.of("Anna", "Beat"));
    }

    // ---------- oeffentliche Seiten ----------

    @Test
    void startseiteWirdGerendert() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Smoke-Test-Jagd")));
    }

    /**
     * Der Abschluss läuft wie der Start nur über das Scannen eines QR-Codes —
     * keine Teilnehmerseite darf einen anklickbaren Weg dorthin anbieten.
     */
    @Test
    void keineSeiteVerlinktDenAbschlussDirekt() throws Exception {
        String startseite = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        org.junit.jupiter.api.Assertions.assertFalse(startseite.contains("href=\"/checkout\""),
                "Die Startseite darf /checkout nicht verlinken.");

        Question zweiterPosten = huntService.getQuestions(hunt).get(1);
        participantService.checkAnswer(participant.getCode(), question.getToken(), "Informatik");
        participantService.checkAnswer(participant.getCode(), zweiterPosten.getToken(), "42");

        String letzterPosten = mockMvc.perform(get("/q/" + zweiterPosten.getToken())
                        .cookie(new Cookie("sjcode", participant.getCode())))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ziel-QR-Code")))
                .andReturn().getResponse().getContentAsString();
        org.junit.jupiter.api.Assertions.assertFalse(letzterPosten.contains("href=\"/checkout\""),
                "Die Postenseite darf /checkout nach dem letzten Posten nicht verlinken.");
    }

    @Test
    void anmeldungLegtTeilnehmerAnUndLeitetWeiter() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("groupName", "Neue Gruppe")
                        .param("members", "Beat"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/welcome"));
    }

    /** 1 bis 5 Namen sind erlaubt — Grenzen serverseitig geprüft, nicht nur im Formular. */
    @Test
    void anmeldungPrueftDieAnzahlNamenServerseitig() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("groupName", "Ohne Namen"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("mindestens einen Namen")));

        mockMvc.perform(post("/register").with(csrf())
                        .param("groupName", "Zu viele")
                        .param("members", "A", "B", "C", "D", "E", "F"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Höchstens 5 Namen")));

        mockMvc.perform(post("/register").with(csrf())
                        .param("groupName", "Fünf Namen")
                        .param("members", "A", "B", "C", "D", "E"))
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

    /**
     * Wer den falschen Posten scannt, soll das sofort beim Laden der Seite
     * sehen — nicht erst nach dem Abschicken einer Antwort. Weder die
     * gescannte noch die eigentlich fällige Frage darf dabei zu lesen sein.
     */
    @Test
    void scannenDesFalschenPostensZeigtSofortDenHinweis() throws Exception {
        Question zweiterPosten = huntService.getQuestions(hunt).get(1);

        mockMvc.perform(get("/q/" + zweiterPosten.getToken())
                        .cookie(new Cookie("sjcode", participant.getCode())))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Das ist nicht dein Posten")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Zimmer 1")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Welches Fach?"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Eine Zahl?"))));
    }

    /** Nach dem letzten Posten soll das Scannen alter Codes auf das Ziel verweisen, nicht auf einen falschen Posten. */
    @Test
    void nachAllenPostenVerweistDasScannenAufsZiel() throws Exception {
        Question zweiterPosten = huntService.getQuestions(hunt).get(1);
        participantService.checkAnswer(participant.getCode(), question.getToken(), "Informatik");
        participantService.checkAnswer(participant.getCode(), zweiterPosten.getToken(), "42");

        mockMvc.perform(get("/q/" + question.getToken()).cookie(new Cookie("sjcode", participant.getCode())))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("bereits alle Posten gelöst")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ziel-QR-Code")));
    }

    /** Ohne bekannten Code (z.B. anderer Browser) lässt sich nicht prüfen, wer scannt — die Frage wird trotzdem gezeigt. */
    @Test
    void ohneBekanntenCodeWirdDiePostenseiteTrotzdemGezeigt() throws Exception {
        mockMvc.perform(get("/q/" + question.getToken()))
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

    /** Bei einer richtigen Antwort verschwinden Frage und Formular zugunsten von "Bravo!". */
    @Test
    void richtigeAntwortZeigtBravoUndVersstecktFrageUndFormular() throws Exception {
        String seite = mockMvc.perform(post("/q/" + question.getToken() + "/answer").with(csrf())
                        .param("code", participant.getCode())
                        .param("answer", "Informatik"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertTrue(seite.contains(">Bravo!<"), "Bravo! fehlt nach richtiger Antwort.");
        org.junit.jupiter.api.Assertions.assertFalse(seite.contains("Welches Fach?"),
                "Die Frage darf nach einer richtigen Antwort nicht mehr zu sehen sein.");
        org.junit.jupiter.api.Assertions.assertFalse(seite.contains("id=\"sendButton\""),
                "Das Antwortformular darf nach einer richtigen Antwort nicht mehr zu sehen sein.");
    }

    /** Eine falsche Antwort zeigt kein "Bravo!" — Frage und Formular bleiben, damit erneut versucht werden kann. */
    @Test
    void falscheAntwortZeigtKeinBravoUndBehaeltFrageUndFormular() throws Exception {
        String seite = mockMvc.perform(post("/q/" + question.getToken() + "/answer").with(csrf())
                        .param("code", participant.getCode())
                        .param("answer", "Falsch"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertFalse(seite.contains(">Bravo!<"), "Bravo! darf bei falscher Antwort nicht erscheinen.");
        org.junit.jupiter.api.Assertions.assertTrue(seite.contains("Welches Fach?"),
                "Die Frage muss bei falscher Antwort weiter sichtbar sein.");
        org.junit.jupiter.api.Assertions.assertTrue(seite.contains("id=\"sendButton\""),
                "Das Antwortformular muss bei falscher Antwort weiter sichtbar sein.");
    }

    /** Ein erfolgreich angeforderter Tipp ist kein "Bravo!" — die Frage bleibt sichtbar, um sie zu beantworten. */
    @Test
    void erfolgreicherTippZeigtKeinBravoUndBehaeltFrageUndFormular() throws Exception {
        String seite = mockMvc.perform(post("/q/" + question.getToken() + "/hint").with(csrf())
                        .param("code", participant.getCode()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertFalse(seite.contains(">Bravo!<"),
                "Ein erfolgreicher Tipp ist keine richtige Antwort und darf kein Bravo! zeigen.");
        org.junit.jupiter.api.Assertions.assertTrue(seite.contains("Welches Fach?"),
                "Die Frage muss nach einem Tipp weiter sichtbar sein.");
        org.junit.jupiter.api.Assertions.assertTrue(seite.contains("id=\"sendButton\""),
                "Das Antwortformular muss nach einem Tipp weiter sichtbar sein.");
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

    /**
     * Die Teilnehmerliste soll die persönliche Postenreihenfolge mit Nummer und
     * Ort zeigen, damit der Admin live sieht, wo jemand hin muss.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void teilnehmerlisteZeigtPostenreihenfolgeMitOrtUndZahl() throws Exception {
        String seite = mockMvc.perform(get("/admin/participants"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Auf die eigene Zeile eingrenzen: andere Tests dieser Klasse legen in
        // derselben Datenbank eigene Teilnehmende an, die Seite zeigt also alle.
        String zeile = extractParticipantRow(seite, participant.getCode());

        // Beide Posten der Route muessen mit ihrer Nummer und ihrem Ort auftauchen.
        org.junit.jupiter.api.Assertions.assertTrue(zeile.contains("1 (Zimmer 1)"),
                "Erster Posten (Nummer + Ort) fehlt in der Reihenfolge.");
        org.junit.jupiter.api.Assertions.assertTrue(zeile.contains("2 (Lichthof)"),
                "Zweiter Posten (Nummer + Ort) fehlt in der Reihenfolge.");

        // Der aktuelle Posten (Position 1, noch nichts geloest) ist hervorgehoben.
        org.junit.jupiter.api.Assertions.assertTrue(
                zeile.contains("current-post\">1 (Zimmer 1)"),
                "Der aktuelle Posten muss als current-post hervorgehoben sein.");
        org.junit.jupiter.api.Assertions.assertFalse(
                zeile.contains("current-post\">2 (Lichthof)"),
                "Ein noch nicht faelliger Posten darf nicht hervorgehoben sein.");
    }

    /** Grenzt das HTML einer Tabellenzeile ein, erkannt am darin enthaltenen Teilnehmer-Code. */
    private String extractParticipantRow(String html, String code) {
        int codeIndex = html.indexOf(code);
        org.junit.jupiter.api.Assertions.assertTrue(codeIndex >= 0, "Code " + code + " taucht nicht in der Seite auf.");
        int rowStart = html.lastIndexOf("<tr", codeIndex);
        int rowEnd = html.indexOf("</tr>", codeIndex);
        return html.substring(rowStart, rowEnd);
    }

    /** Admin sieht Gruppenname UND die einzelnen Mitgliedernamen, nicht nur einen davon. */
    @Test
    @WithMockUser(roles = "ADMIN")
    void teilnehmerlisteZeigtGruppennameUndMitglieder() throws Exception {
        mockMvc.perform(get("/admin/participants"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Die Testgruppe")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Anna, Beat")));
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
        mockMvc.perform(get("/admin/qr-finish.png"))
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
