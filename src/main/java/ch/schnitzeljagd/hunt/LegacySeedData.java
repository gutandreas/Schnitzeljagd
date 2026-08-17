package ch.schnitzeljagd.hunt;

import java.util.HashMap;

/**
 * Die Fragen der alten Schnitzeljagd-Versionen, unveraendert aus dem frueheren
 * {@code QuestionList} uebernommen. Diese Klasse dient nur noch als einmalige
 * Importquelle beim allerersten Start ({@link LegacySeedImporter}) — danach leben
 * die Fragen in der Datenbank und werden im Adminbereich gepflegt.
 * Sie darf geloescht werden, sobald der Import auf dem Server gelaufen ist.
 */
public class LegacySeedData {

    /** Name der Jagd pro altem Modus — aus den frueheren Konsolenausgaben uebernommen. */
    public static final String[] HUNT_NAMES = {
            "GYM2",
            "EF"
    };

    private static final HashMap<Integer, SeedQuestion> questionMap = new HashMap<>();

    /** Eine Frage, wie sie frueher im Quelltext stand. */
    public record SeedQuestion(String title, String place, String text, String[] answers, String hint) {
    }

    public static void addQuestionsToMap(int modus){

        questionMap.clear();

        switch (modus){
            case 0:
                System.out.println("Modus GYM2 gestartet");
                questionMap.put(1, new SeedQuestion("Einstieg", "Aktuelles Zimmer","Diese Schnitzeljagd findet im Fach ... statt.", new String[]{"Informatik"}, "Es beginnt mit 'I'"));
                questionMap.put(2, new SeedQuestion("Programmierung", "Haupteingang", "Was wird in die Konsole gedruckt?", new String[]{"Aprikose"}, "a=6 und b=12 sind die aktualisierten Werte"));
                questionMap.put(3, new SeedQuestion("Rechnerarchitektur", "Eingang Ergänzungsbau", "Welches Wort bringt die LED zum Leuchten?", new String[]{"jodeln"}, "Einer der Buchstaben ist 'j'"));
                questionMap.put(4, new SeedQuestion("Datenbanken", "Getränkeautomat EG", "Welches Wort liefert die SQL-Abfrage?", new String[]{"SEKTE"}, "ORDER BY ... DESC ordnet alles vom grössten zum kleinsten."));
                questionMap.put(5, new SeedQuestion("Netzwerke", "Lichthof", "Was liefern die Buchstaben der korrekten Antworten?", new String[]{"Brett"}, "Das Lösungswort besteht aus Holz"));
                questionMap.put(6, new SeedQuestion("Verschlüsselung", "022", "Wie lautet das Wort mit der Nummer der verschlüsselten Nachricht?", new String[]{"Birne"}, "Bobs Zahlen sind nicht von Bedeutung"));
                questionMap.put(7, new SeedQuestion("Künstliche Intelligenz", "Eingang Neubau", "Wie viele Situationen enthält der Baum in den Blättern?", new String[]{"160000", "160'000"}, "Es sind mehr als 100000"));
                questionMap.put(8, new SeedQuestion("Textcodierung", "Billardtisch", "Wofür steht der Binärcode in ASCII?", new String[]{"TOMATE"}, "Ketchup"));
                questionMap.put(9, new SeedQuestion("Bildcodierung", "Eingang bei Veloständer", "Für welche Farbe steht der RGB-Wert?", new String[]{"Grau"}, "Augenkrankheit *-er Star"));
                questionMap.put(10, new SeedQuestion("Cyberattacken", "Ausgang zum Innenhof", "Wie viele Versuche braucht es maximal?", new String[]{"125000", "125'000"}, "_ mal _ mal _"));
                questionMap.put(11, new SeedQuestion("Modelle und Simulationen", "Sekretariat", "Welches Prinzip wurde auf den Kreis angewandt?", new String[]{"Diskretisierung", "diskretisiert"}, "Es beginnt mit 'D'"));
                break;

            case 1:
                System.out.println("Modus EF");
                questionMap.put(1, new SeedQuestion("Einstieg", "Heutiges Informatikzimmer","In welchem Schulfach befindest du dich gerade?", new String[]{"Informatik"}, "Es beginnt mit 'I'"));
                questionMap.put(2, new SeedQuestion("Programmierung", "Haupteingang", "Was wird in der Konsole ausgegeben?", new String[]{"JUSTjava"}, "StringBuilder.append() fügt einen Buchstabe am Ende des String hinzu; StringBuilder.insert() fügt einen Buchstabe an gewünschter Stelle hinzu; Strinbuilder.setCharAt() ändert einen Buchstabe an gewünschter Stelle ab."));
                questionMap.put(3, new SeedQuestion("Codierung", "Vor Zimmer 010","Wie lautet das Lösungswort?", new String[]{"ZOO"}, "Frage 1 basiert auf dem additiven Farbmischsystem."));
                questionMap.put(4, new SeedQuestion("Datenbanken", "Lichthof","Löse die Aufgabe auf dem Blatt!", new String[]{"1001011101010100"}, "Die Zeile mit Benno muss nicht berücksichtigt werden."));
                questionMap.put(5, new SeedQuestion("Cyberattacken", "Beim Treppenhaus (EG)","Wie lautet das Lösungswort?", new String[]{"Geschafft"}, "Brauchst du nicht... Denk nach!"));
                questionMap.put(6, new SeedQuestion("Big Data", "Eingang Neubau","Max ist auf der Suche nach einem neuen Film. Welcher könnte ihm gefallen? Die Anfangsbuchstaben von einem der vier Namen kombiniert mit den beiden Filmen (als Buchstabe), die Max gefallen könnten, ergibt den Namen eines Produzenten, der Filme herstellt, welche Max ebenfalls gefallen könnte.", new String[]{"Marvel"}, "Die zwei Buchstaben, welche die beiden Filme repräsentieren, die Max gefallen könnten gehören zu den letzten beiden Buchstaben des Wortes."));
                questionMap.put(7, new SeedQuestion("Rechnerarchitektur", "Heutiges Informatikzimmer","Finde die drei 7-stelligen Binärzahlen, die jeweils einzeln eine der LED's anzünden. Jede dieser 7-stelligen Binärzahlen muss dann mit der ASCII-Tabelle in ein Buchstaben umgewandelt werden. Das Lösungswort setzt sich aus den drei Buchstaben zusammen.", new String[]{"tod"}, "Probiere von den LED's zurück zu arbeiten."));
                questionMap.put(8, new SeedQuestion("Verschlüsselung", "Vor Sekretariat","Was ist der Klartext dieser Verschlüsselung?", new String[]{"Klabautermann"}, "Binärzahl in Zahl umwandeln, um in der Tabelle zu schauen"));
                questionMap.put(9, new SeedQuestion("Objektorientierung", "Bei Selectaautomaten (EG)","Beantworte die Fragen auf dem Blatt?", new String[]{"Kette"}, "Der letzte Buchstaben des Lösungsworts ist ein 'E'"));
                questionMap.put(10, new SeedQuestion("Netzwerke", "Tür zum Innenhof (Neben FS-Zimmer Musik)","Auf welcher Portnummer können normalerweise Webseiten verschlüsselt abgerufen werden?", new String[]{"443"}, "Es ist eine dreistellige Zahl..."));
                break;

        }

    }

    public static SeedQuestion getQuestionByNumber(int number){
        return questionMap.get(number);
    }

    public static int getTotalNumberOfQuestions(){
        return questionMap.size();
    }

}
