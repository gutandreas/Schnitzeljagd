package ch.schnitzeljagd.participant;

import ch.schnitzeljagd.hunt.Hunt;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Reiner Objekttest ohne Spring-Kontext — Hunt und Participant lassen sich
 * direkt konstruieren, ein "jetzt" wird als fester Zeitpunkt übergeben statt
 * mit Thread.sleep gewartet zu werden.
 */
class ParticipantTest {

    @Test
    void elapsedSecondsBeruecksichtigtTippzuschlagUndFriertNachAbschlussEin() {
        Hunt hunt = new Hunt("Testjagd");
        Participant participant = new Participant("ABCD", "Die Testgruppe", List.of("Anna", "Beat"), hunt, List.of(1L, 2L));
        LocalDateTime start = participant.getStart();

        assertEquals(125, participant.getElapsedSeconds(start.plusSeconds(125)));

        participant.requestHint(1L);
        assertEquals(185, participant.getElapsedSeconds(start.plusSeconds(125)),
                "Der Tippzuschlag muss sofort im Timer-Wert stecken.");

        // Denselben Tipp nochmals anfordern (z.B. Seite neu geladen) darf nicht nochmal kosten.
        participant.requestHint(1L);
        assertEquals(185, participant.getElapsedSeconds(start.plusSeconds(125)));

        participant.markSolved();
        participant.markSolved();
        participant.finish();

        long frozen = participant.getElapsedSeconds(LocalDateTime.now().plusDays(1));
        assertTrue(frozen >= 60, "Muss den Tippzuschlag weiterhin enthalten: " + frozen);
        assertEquals(frozen, participant.getElapsedSeconds(LocalDateTime.now().plusDays(2)),
                "Nach dem Abschluss darf sich der Timer-Wert nicht mehr ändern.");
    }
}
