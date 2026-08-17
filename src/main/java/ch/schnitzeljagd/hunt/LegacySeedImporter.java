package ch.schnitzeljagd.hunt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Überträgt die früher im Quelltext stehenden Fragen einmalig in die Datenbank.
 * Läuft nur, solange noch gar keine Jagd existiert — danach ist die Datenbank
 * die alleinige Quelle und wird im Adminbereich gepflegt.
 */
@Component
class LegacySeedImporter implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(LegacySeedImporter.class);

    private final HuntService huntService;

    LegacySeedImporter(HuntService huntService) {
        this.huntService = huntService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!huntService.getHunts().isEmpty()) {
            return;
        }

        log.info("Keine Jagd vorhanden — importiere die alten Fragensaetze.");

        Long firstHuntId = null;
        for (int modus = 0; modus < LegacySeedData.HUNT_NAMES.length; modus++) {
            LegacySeedData.addQuestionsToMap(modus);
            int total = LegacySeedData.getTotalNumberOfQuestions();
            if (total == 0) {
                continue;
            }

            Hunt hunt = huntService.createHunt(LegacySeedData.HUNT_NAMES[modus]);
            if (firstHuntId == null) {
                firstHuntId = hunt.getId();
            }

            for (int number = 1; number <= total; number++) {
                LegacySeedData.SeedQuestion seed = LegacySeedData.getQuestionByNumber(number);
                // Fester statt zufaellig erzeugter Code — siehe LegacySeedData.getTokenForQuestion.
                huntService.addQuestion(
                        hunt.getId(),
                        seed.title(),
                        seed.place(),
                        seed.text(),
                        seed.hint(),
                        String.join("; ", seed.answers()),
                        LegacySeedData.getTokenForQuestion(modus, number));
            }
            log.info("Jagd '{}' mit {} Posten importiert.", hunt.getName(), total);
        }

        if (firstHuntId != null) {
            huntService.activateHunt(firstHuntId);
        }
    }
}
