package ch.schnitzeljagd;

import ch.schnitzeljagd.hunt.Hunt;
import ch.schnitzeljagd.hunt.HuntService;
import ch.schnitzeljagd.participant.Participant;
import ch.schnitzeljagd.participant.ParticipantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Das Profil "test" ergaenzt die Haupt-Properties (H2 im Arbeitsspeicher).
// Eine test/application.properties wuerde die Haupt-Properties dagegen ersetzen.
@SpringBootTest
@ActiveProfiles("test")
class SchnitzeljagdApplicationTests {

	@Autowired
	private HuntService huntService;

	@Autowired
	private ParticipantService participantService;

	@Test
	void contextLoads() {
		assertNotNull(huntService);
	}

	@Test
	void derImportLegtDieAltenFragensaetzeAn() {
		// Der LegacySeedImporter laeuft beim Start; danach muss mindestens eine Jagd aktiv sein.
		assertFalse(huntService.getHunts().isEmpty());
		assertTrue(huntService.getActiveHunt().isPresent());
	}

	/**
	 * Wacht ueber ein fehlendes {@code break;} im alten switch-Block: Ohne das
	 * fiel LZG4 in Kantifest durch, und LZG4 zeigte fremde Fragen. Der Test
	 * prueft die Anzahl Posten je Jagd und dass der Einstieg der richtige ist.
	 */
	@Test
	void jedeImportierteJagdHatIhreEigenenFragen() {
		Hunt lzg4 = findeJagd("LZG4");
		Hunt kantifest = findeJagd("Kantifest");

		assertEquals(11, huntService.getQuestions(lzg4).size());
		assertEquals(9, huntService.getQuestions(kantifest).size());

		String einstiegLzg4 = huntService.getQuestions(lzg4).get(0).getText();
		String einstiegKantifest = huntService.getQuestions(kantifest).get(0).getText();
		assertTrue(einstiegLzg4.contains("findet im Fach"), "LZG4 zeigt fremde Fragen: " + einstiegLzg4);
		assertTrue(einstiegKantifest.contains("Fachschaft"), "Kantifest zeigt fremde Fragen: " + einstiegKantifest);
	}

	private Hunt findeJagd(String name) {
		return huntService.getHunts().stream()
				.filter(h -> h.getName().equals(name))
				.findFirst()
				.orElseThrow(() -> new AssertionError("Jagd '" + name + "' wurde nicht importiert."));
	}

	@Test
	void einDurchlaufVonDerAnmeldungBisZumAbschluss() {
		Hunt hunt = huntService.createHunt("Testjagd");
		huntService.addQuestion(hunt.getId(), "Einstieg", "Zimmer 1", "Wie heisst das Fach?", null, "Informatik");
		huntService.addQuestion(hunt.getId(), "Zweiter", "Lichthof", "Nenne eine Zahl.", null, "42; zweiundvierzig");
		huntService.activateHunt(hunt.getId());

		Participant participant = participantService.register("Anna", "Muster");
		assertEquals(2, participant.getTotal());

		// Falscher Posten: der zweite Posten wird gescannt, obwohl der erste dran ist.
		String firstToken = huntService.getQuestion(participant.getCurrentQuestionId()).getToken();
		String otherToken = huntService.getQuestions(hunt).stream()
				.map(q -> q.getToken())
				.filter(t -> !t.equals(firstToken))
				.findFirst()
				.orElseThrow();
		assertFalse(participantService.checkAnswer(participant.getCode(), otherToken, "42").success());

		// Abschliessen geht erst, wenn alle Posten geloest sind.
		assertFalse(participantService.finish(participant.getCode()).success());

		// Beide Posten korrekt beantworten — Gross-/Kleinschreibung spielt keine Rolle.
		assertTrue(participantService.checkAnswer(participant.getCode(), firstToken, "informatik").success());
		assertTrue(participantService.checkAnswer(participant.getCode(), otherToken, "ZWEIUNDVIERZIG").success());

		assertTrue(participantService.finish(participant.getCode()).success());
		assertEquals(1, participantService.getRanking().size());
	}

	@Test
	void einTippKostetEineMinuteUndNurEinmal() {
		Hunt hunt = huntService.createHunt("Tippjagd");
		huntService.addQuestion(hunt.getId(), "Einstieg", "Zimmer 1", "Eine Frage?", "Der Tipp", "antwort");
		huntService.activateHunt(hunt.getId());

		Participant participant = participantService.register("Tim", "Tipp");
		String token = huntService.getQuestion(participant.getCurrentQuestionId()).getToken();

		assertTrue(participantService.requestHint(participant.getCode(), token).success());
		// Nochmals anfordern (z.B. Seite neu geladen) darf nicht ein zweites Mal kosten.
		assertTrue(participantService.requestHint(participant.getCode(), token).success());

		assertTrue(participantService.checkAnswer(participant.getCode(), token, "antwort").success());
		assertTrue(participantService.finish(participant.getCode()).success());

		Participant nachher = participantService.findByCode(participant.getCode()).orElseThrow();
		assertEquals(1, nachher.getHintCount());
		assertTrue(nachher.getDuration().getSeconds() >= 60,
				"Der Zuschlag fehlt, die Dauer war " + nachher.getDuration());
		assertTrue(nachher.getDuration().getSeconds() < 120,
				"Der Zuschlag wurde doppelt verrechnet: " + nachher.getDuration());
	}

	@Test
	void ohneTippGibtEsKeinenZuschlag() {
		Hunt hunt = huntService.createHunt("Jagd ohne Tipp");
		huntService.addQuestion(hunt.getId(), "Einstieg", "Zimmer 1", "Eine Frage?", "Der Tipp", "antwort");
		huntService.activateHunt(hunt.getId());

		Participant participant = participantService.register("Ohne", "Tipp");
		String token = huntService.getQuestion(participant.getCurrentQuestionId()).getToken();
		participantService.checkAnswer(participant.getCode(), token, "antwort");
		participantService.finish(participant.getCode());

		Participant nachher = participantService.findByCode(participant.getCode()).orElseThrow();
		assertEquals(0, nachher.getHintCount());
		assertTrue(nachher.getDuration().getSeconds() < 60, "Unerwarteter Zuschlag: " + nachher.getDuration());
	}

	@Test
	void einFalscherCodeWirdAbgewiesen() {
		assertFalse(participantService.checkAnswer("XXXX", "egal", "egal").success());
		assertFalse(participantService.finish("XXXX").success());
	}
}
