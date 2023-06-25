package com.bezkoder.spring.thymeleaf;

import java.util.HashMap;

public class QuestionList {

    private static final HashMap<Integer, Question> questionMap = new HashMap<>();
    private static final HashMap<String, Integer> encryptionMap = new HashMap<>();
    static {
        addQuestionsToMap(1);
        addEncryptedKeysToMap();
    }

    public static void addQuestionsToMap(int modus){

        questionMap.clear();

        switch (modus){
            case 1:
                System.out.println("Modus Kantifest gestartet");
                questionMap.put(1, new Question("Einstieg", "Zimmer 013","Diese Schnitzeljagd ist von der Fachschaft des Fachs ... organisiert.", new String[]{"Informatik"}, "Es beginnt mit 'I'"));
                questionMap.put(2, new Question("Leuchtendes Lämpchen", "Vor Bibliothek", "Wie heisst das Lösungswort, mit dem das Lämpchen leuchtet?", new String[]{"jodeln"}, "Beginne beim Schaltplan vom Lämpchen her. Ein gesuchter Buchstabe ist 'j' (1101010)."));
                questionMap.put(3, new Question("Zahleneigenschaft", "Zimmer 010 (Aufgabe 'Zahleneigenschaft')","Welche Eigenschaft muss die erkannte Zahl haben, damit Musik gespielt wird?", new String[]{"primzahl", "primzahlen", "prim"}, "Es hat etwas mit der Teilbarkeit zu tun."));
                questionMap.put(4, new Question("Distanz", "Zimmer 010 (Aufgabe 'Distanz')", "Welche Eigenschaft muss die Distanz haben, damit Musik gespielt wird? (In einem Wort)", new String[]{"quadrat", "quadratzahl"}, "Das 'Gegenteil' der Wurzel..."));
                questionMap.put(5, new Question("Mimik", "Zimmer 010 (Aufgabe 'Mimik')","Was muss man tun, damit die Musik abgespielt wird?", new String[]{"lächeln", "lachen", "smile"}, "Schau nicht so böse..."));
                questionMap.put(6, new Question("Menschen 'scannen'", "Zimmer 010 (Aufgabe 'Menschen scannen')","Welche Eigenschaft muss der erkannte Mensch haben, damit Musik gespielt wird?", new String[]{"alt", "älter", "alte menschen", "greis"}, "Es ist möglich, dass Bilder aus dem Internet in die Kamera gezeigt werden müssen, weil du selbst die Eigenschaft nicht erfüllst..."));
                questionMap.put(7, new Question("Informatik-Hangman", "Galerie 1. Etage","Spiele Hangman und errate Informatikbegriffe! Hast du alle erraten, so erhältst du das Lösungswort.", new String[]{"nohtyp"}, "Die Begriffe sind wichtige Programmstrukturen von der Programmiersprache Python."));
                questionMap.put(8, new Question("Legend of the wild sea", "Zimmer 013 (Aufgabe 'Legend of the Wild Sea'","Bring den Piraten auf sein Boot zurück, sodass du am Schluss das Lösungswort erhältst.", new String[]{"flashback"}, "Das schaffst du auch ohne Tipps..."));
                questionMap.put(9, new Question("Anidex", "Vor dem Zimmer 212","Beantworte folgende Fragen und scanne das korrekte Foto mit der Web App www.inf4u.ch/anidex um das Lösungswort zu erhalten.", new String[]{"kss"}, "1. Vektor- oder Pixelgrafik? 2. Wie hast du solche Fotos von Tieren verglichen, als du noch ein Kind warst? 3. Beachte die Mengeneinheiten: 1 Byte = 8 Bit und 1 Minute = 60 Sekunden."));
                break;
            case 2:
                System.out.println("Modus G21s Gruppe A gestartet (Aufgaben von Gruppe B)");
                questionMap.put(1, new Question("Einstieg", "Heutiges Informatikzimmer","In welchem Schulfach befindest du dich gerade?", new String[]{"Informatik"}, "Es beginnt mit 'I'"));
                questionMap.put(2, new Question("Programmierung", "Haupteingang", "Auf dem Bild siehst du ein Programm, das in Java geschrieben wurde. Bestimme, welche Buchstaben schlussendlich in welcher Reihenfolge ausgegeben werden. Diese entsprechen dem Lösungswort.", new String[]{"schiff"}, "Gehe unter dem Klassenteam von der Klasse G21s im Fach Informatik auf die Dateien, öffne das Skript Python und lese das komplette Skript noch einmal durch. Viel Spass :)"));
                questionMap.put(3, new Question("Verschlüsselung", "Vor Sekretariat","Was bedeutet „Pruf“ eigentlich, wenn die ersten drei Buchstaben um drei Stellen verschoben wurde (Cäsar-Chiffren) und der Vierte um zwei Stellen?", new String[]{"mord"}, "Es ist ein Verbrechen."));
                questionMap.put(4, new Question("Codierung", "Vor Zimmer 010","Wie schreibt man die folgenden Zahlen in Hexadezimal? 1010 1111 1111 1110", new String[]{"affe"}, "Hexadezimal = 16er – System"));
                questionMap.put(5, new Question("Netzwerke", "Lichthof","Die Blöcke einer gültigen IPv4-Adresse liegen zwischen 0 und ...?", new String[]{"255", "256"}, "Es ist eine 2er-Potenz minus 1"));
                questionMap.put(6, new Question("Datenbanken", "Tische im Gang (EG)","SELECT lieblingsbuchstabe WHERE geschlecht = 'M' AND alter > 30 ORDER BY alter", new String[]{"wort"}, "Das Lösungswort hat vier Buchstaben."));
                questionMap.put(7, new Question("Cyberattacken", "Eingang Neubau","Bei welcher Cyberattacke werden Webseiten oder Mails gefälscht, so dass der Benutzer glaubt, mit jemand anderem zu kommunizieren, als er es eigentlich tut.", new String[]{"phishing"}, "Das Lösungswort beginnt mit 'P'"));
                questionMap.put(8, new Question("Rechnerarchitektur", "Heutiges Informatikzimmer","Wie heisst das Lösungswort, mit dem das Lämpchen leuchtet?", new String[]{"jodeln"}, "Beginne beim Schaltplan vom Lämpchen her. Ein gesuchter Buchstabe ist 'j' (1101010).")); // Rätsel funktioniert nicht
                questionMap.put(9, new Question("Modelle und Simulationen", "Bei Selectaautomaten (EG)","Du gehst mit deinen Freunden Kühe reiten. Auf einer Kuh haben 3 Leute Platz, ihr habt jedoch Bedenken, dass die Kühe eurem Gewicht nicht standhalten können, weshalb ihr nur zu zweit auf eine Kuh steigt. Es warten 17 Personen darauf, einen Kuhritt zu betätigen, es stehen aber nur 4 Kühe bereit. Ein Kuhritt dauert fünf Minuten.\n" +
                        "Wie viele Minuten dauert es, bis auch die letzte Person ihren Ritt durchführen konnte? Du musst nur die Zahl eingeben.", new String[]{"15"}, "Die Zahl ist grösser als 10"));
                questionMap.put(10, new Question("Künstliche Intelligenz", "Tür zum Innenhof (Neben FS-Zimmer Musik)","Wie heisst der Autor mit deinem Wort als Nachnamen zum Vornamen?", new String[]{"Michael", "Michael Ende"}, "Dieser Autor schrieb die un... Geschichte"));
                break;
            case 3:
                System.out.println("Modus G21s Gruppe B gestartet (Aufgaben von Gruppe A)");
                questionMap.put(1, new Question("Einstieg", "Heutiges Informatikzimmer","In welchem Schulfach befindest du dich gerade?", new String[]{"Informatik"}, "Es beginnt mit 'I'"));
                questionMap.put(2, new Question("Programmierung", "Haupteingang", "Wie alt ist Karl?", new String[]{"13"}, "Karl darf noch kein Bier trinken."));
                questionMap.put(3, new Question("Verschlüsselung", "Vor Sekretariat","Wie viele Punkte hatte Marius (fiktiv) in der letzten Informatikprüfung erreicht?", new String[]{"29"}, "Welche Nummer siehst du?  Hinter dem richtigen Binärcode befindet sich die Lösung zur Frage."));
                questionMap.put(4, new Question("Codierung", "Vor Zimmer 010","Löse die Aufgabe auf dem Blatt", new String[]{"pixel"}, "Es gibt ein Smartphone, das so heisst."));
                questionMap.put(5, new Question("Netzwerke", "Lichthof","Wie lautet die vollständige IP-Adresse?", new String[]{"192.100.27.7"}, "Brauchst du nicht..."));
                questionMap.put(6, new Question("Datenbanken", "Tische im Gang (EG)","Welche Zahl entsteht, wenn alle ArtId der nach dem Befehl in der Tabelle übrigbleibenden Artikel addiert werden?\n" +
                        "SELECT artId FROM lebensmittelladen WHERE preis > 3.55 OR lagerbestand < 29", new String[]{"37"}, "Es werden 6 Zahlen addiert..."));
                questionMap.put(7, new Question("Cyberattacken", "Eingang Neubau","Um was für eine Cyberattake handelt es sich bei dem Trojaner?", new String[]{"Schadsoftware"}, "Das Trojanische Pferd aus der Geschichte verhielt sich gleich wie heutzutage ein Trojaner."));
                questionMap.put(8, new Question("Big Data", "Verbindungsgang zu Turnhalle (EG)","Wenn man alle Daten, die täglich von Android-Geräten gesammelt werden zu einem Laptopturm aufstapeln würde, mit welchem Bau könnte man die Höhe vergleichen?", new String[]{"Eiffelturm"}, "Steht in Frankreich..."));
                questionMap.put(9, new Question("Rechnerarchitektur", "Heutiges Informatikzimmer","Welches Lösungswort ergeben die LEDs, die leuchten? Sie leuchten bei einer 1.", new String[]{"Input"}, "O und 2 gehören nicht zum Lösungswort..."));
                questionMap.put(10, new Question("Modelle und Simulationen", "Bei Selectaautomaten (EG)","Für die Qualität der Piste wurden Zahlen von 1 – 3 verwendet (schlecht – sehr gut). Wenn man aber mit diesen Zahlen etwas berechnen würde, würde es die Rechnung verfälschen. Berechne die Zahl für die Pistenqualität mit der Qualität 3 auf 3 Nachkommastellen genau.", new String[]{"0.955"}, "0.9..."));
                questionMap.put(11, new Question("Künstliche Intelligenz", "Tür zum Innenhof (Neben FS-Zimmer Musik)","Wie war der Nachname, einer der bekanntesten Informatiker, welcher auch einen Test entwickelte, um zu entscheiden, ob ein Programm intelligent ist?\n", new String[]{"Turing"}, "Das brauchst du nicht..."));
                break;
            case 4:
                System.out.println("Modus G21a Gruppe A gestartet (Aufgaben von Gruppe B)");
                questionMap.put(1, new Question("Einstieg", "Heutiges Informatikzimmer","In welchem Schulfach befindest du dich gerade?", new String[]{"Informatik"}, "Es beginnt mit 'I'"));
                questionMap.put(2, new Question("Programmierung", "Haupteingang", "Welches Wort wird beim «print(solutien)» ausgegeben?", new String[]{"apfelbaum"}, "Das erste Wort wird nicht gebraucht"));
                questionMap.put(3, new Question("Verschlüsselung", "Vor Sekretariat","Entschlüssle das Lösungswort", new String[]{"Fisch"}, "Zuerst die Buchstaben entschlüsseln"));
                questionMap.put(4, new Question("Codierung", "Vor Zimmer 010","Finde das folgende Wort", new String[]{"SONNENSCHEIN:)"}, "Suche weiter… Es gibt auch Sonderzeichen!"));
                questionMap.put(5, new Question("Netzwerke", "Lichthof","Welche dieser 10 lokalen IP-Adressen sind gültig. Verwende den passenden Buchstaben, der richtigen IP-Adresse und bilde ein Wort.", new String[]{"oase"}, "Das Wort hat 4 Buchstaben"));
                questionMap.put(6, new Question("Datenbanken", "Tische im Gang (EG)","Addiere von den ausgewählten Büchern die Erscheinungsjahre. Welche Zahl kommt raus?\n" +
                        "SELECT Erscheinungsjahr FROM Bücher WHERE Titel LIKE \"D%\"", new String[]{"8008"}, "Das Prozentzeichen steht dafür, dass alles nach dem \"D\" egal ist."));
                questionMap.put(7, new Question("Cyberattacken", "Eingang Neubau","Wie lautet das Lösungswort, das sich durch die Buchstaben ergibt?", new String[]{"maus"}, "Piep, piep..."));
                questionMap.put(8, new Question("Big Data", "Verbindungsgang zu Turnhalle (EG)","Auf der Welt gibt es ca. 2.2 Milliarden Smartphones. Jedes Gerät liefert täglich 4.4MB an Daten. Die Datenmenge insgesamt pro Tag liegt bei 10’000TB, also etwa so viel wie auf 10'000 Laptops gespeichert werden kann. Wenn man mit all diesen 10'000 Laptops einen Turm baut, wäre er gleich hoch wie welcher bekannte Turm?", new String[]{"Eiffelturm"}, "Er steht in Richtung Nordwesten von hier..."));
                questionMap.put(9, new Question("Rechnerarchitektur", "Heutiges Informatikzimmer","Bei welchen Inputs muss man Strom geben, damit die 2. Lampe brennt? Schreibe die Lösung als Binärzahl (z.B. 0101)", new String[]{"1101"}, "Input 4 muss nicht beachtet werden."));
                questionMap.put(10, new Question("Modelle und Simulationen", "Bei Selectaautomaten (EG)","Was ist das Lösungswort?", new String[]{"SIU"}, "Cristiano Ronaldo..."));
                questionMap.put(11, new Question("Künstliche Intelligenz", "Tür zum Innenhof (Neben FS-Zimmer Musik)","Löse dieses Quiz um am Schluss auf das richtige Lösungswort zu kommen, Viel Spass!", new String[]{"programm"}, "In der Informatik steht das ... im Mittelpunkt"));
                break;
            case 5:
                System.out.println("Modus G21a Gruppe B gestartet (Aufgaben von Gruppe A)");
                questionMap.put(1, new Question("Einstieg", "Heutiges Informatikzimmer","In welchem Schulfach befindest du dich gerade?", new String[]{"Informatik"}, "Es beginnt mit 'I'"));
                questionMap.put(2, new Question("Programmierung", "Haupteingang", "Was wird ausgegeben wenn man den Knopf drückt welcher einen schwarzen Hintergrund hat?", new String[]{"Sepp"}, "Viele Sachen kann man streichen"));
                questionMap.put(3, new Question("Verschlüsselung", "Vor Sekretariat","Übersetze die folgende Geheimschrift zurück ins Original: Mfkyukqnhmyajwxnhmjwzsl", new String[]{"Haftpflichtversicherung"}, "Alle Buchstaben wurden um 5 Stellen verschoben"));
                questionMap.put(4, new Question("Codierung", "Vor Zimmer 010","Decodiere das mit ASCII-Code codierte Lösungswort", new String[]{"Abgaben"}, "Zu einem Zeichen gehören immer 7 Bits"));
                questionMap.put(5, new Question("Netzwerke", "Lichthof","Wie viele dieser Angaben hat Tobias selber in ein Eingabefeld auf dem Server eingetippt? Hänge an diese Zahl zudem den Port an, an den man beim Aufruf von «http://www.sbb.ch» einen Get-Request schickt.", new String[]{"380"}, "Schickt man im Browser einen HTTP-Request an eine IP-Adresse ohne eine Portnummer anzugeben, so wird automatisch eine Portnummer ergänzt."));
                questionMap.put(6, new Question("Cyberattacken", "Eingang Neubau","Du führst eine ‘Man in the Middle‘-Attacke aus. Dazu musst du die folgenden 6 Kästchen in die richtige Reihenfolge bringen, um Alice und Bob auszuspionieren. 2 Beispiele sind schon vorgegeben. Wie lautet das Lösungswort?", new String[]{"SPINAT"}, "Grün..."));
                questionMap.put(7, new Question("Big Data", "Beim Treppenhaus (EG)","Ordne die Cookies zur passenden Beschreibung. Welche der Beschreibungen passt zu den Performance Cookies? Gib die Nummer der richtigen Beschreibung an.", new String[]{"4"}, "Das brauchst du nicht..."));
                questionMap.put(8, new Question("Rechnerarchitektur", "Heutiges Informatikzimmer","Welche Zahlen müssen oben eingegeben werden, damit die Lampe am Ende aufleuchtet.", new String[]{"1111"}, "Es enthält mindestens 3x die 1"));
                questionMap.put(9, new Question("Modelle und Simulationen", "Bei Selectaautomaten (EG)","Beim Erstellen von Modellen und Simulationen gibt es 6 unterschiedliche Prozessschritte. Hänge jeweils den Anfangsbuchstaben des entsprechenden Schritts aneinander und du erhältst das Lösungswort.", new String[]{"MBIVVE"}, "Es beginnt mit M"));
                questionMap.put(10, new Question("Künstliche Intelligenz", "Tür zum Innenhof (Neben FS-Zimmer Musik)","Addiere alle Ausgabewerte zusammen (wenn es durch den Schwellenwert aufgehalten wird, zählt es logischerweise nicht.)", new String[]{"2.8", "2,8"}, "Knoten 1 und 2 kannst du ignorieren..."));
                break;
            case 6:
                System.out.println("Modus G21b");
                questionMap.put(1, new Question("Einstieg", "Heutiges Informatikzimmer","In welchem Schulfach befindest du dich gerade?", new String[]{"Informatik"}, "Es beginnt mit 'I'"));
                questionMap.put(2, new Question("Programmierung", "Haupteingang", "Wie gross ist c?", new String[]{"69"}, "Die Variable «range» funktioniert folgendermassen: range(Anfangszahl, Endzahl, Grösse der Schritte)"));
                questionMap.put(3, new Question("Verschlüsselung", "Vor Sekretariat","Übersetze die folgende Geheimschrift zurück ins Original: Mfkyukqnhmyajwxnhmjwzsl", new String[]{"Haftpflichtversicherung"}, "Alle Buchstaben wurden um 5 Stellen verschoben"));
                questionMap.put(4, new Question("Codierung", "Vor Zimmer 010","Was wurde hier codiert?", new String[]{"Gut gemacht"}, "G** g******"));
                questionMap.put(5, new Question("Netzwerke", "Lichthof","Welche IP Adressen sind gültig?", new String[]{"INFO"}, "Brauchst du nicht..."));
                questionMap.put(6, new Question("Datenbanken", "Eingang Neubau","Wie lautet das Lösungswort, das durch den SQL-Befehl auf dem Blatt entsteht?", new String[]{"Obstkuchen"}, "Das Lösungswort beginnt mit dem 15. Buchstaben des Alphabets."));
                questionMap.put(7, new Question("Big Data", "Beim Treppenhaus (EG)","Fülle die Lücken des Big-Five-Modell aus. Ordne die Anfangsbuchstaben der Fünf Wörter so, damit es eine französische Gemeinde gibt. Das ist das Lösungswort.", new String[]{"Goven"}, "Das Wort ist auch das schweizerdeutsche Wort für «nervige Kinder»"));
                questionMap.put(8, new Question("Rechnerarchitektur", "Vor Bibliothek", "Wie heisst das Lösungswort, mit dem das Lämpchen leuchtet?", new String[]{"jodeln"}, "Beginne beim Schaltplan vom Lämpchen her. Ein gesuchter Buchstabe ist 'j' (1101010)."));
                questionMap.put(9, new Question("Künstliche Intelligenz", "Tür zum Innenhof (Neben FS-Zimmer Musik)","Welche Zahl wird beim Output Layer ausgegeben?", new String[]{"9"}, "Es wird nur ein Impuls weitergeleitet, wenn der Schwellenwert überschritten wird."));
                break;
            case 7:
                System.out.println("Modus EF");
                questionMap.put(1, new Question("Einstieg", "Heutiges Informatikzimmer","In welchem Schulfach befindest du dich gerade?", new String[]{"Informatik"}, "Es beginnt mit 'I'"));
                questionMap.put(2, new Question("Programmierung", "Haupteingang", "Was wird in der Konsole ausgegeben?", new String[]{"JUSTjava"}, "StringBuilder.append() fügt einen Buchstabe am Ende des String hinzu; StringBuilder.insert() fügt einen Buchstabe an gewünschter Stelle hinzu; Strinbuilder.setCharAt() ändert einen Buchstabe an gewünschter Stelle ab."));
                questionMap.put(3, new Question("Codierung", "Vor Zimmer 010","Wie lautet das Lösungswort?", new String[]{"ZOO"}, "Frage 1 basiert auf dem additiven Farbmischsystem."));
                questionMap.put(4, new Question("Datenbanken", "Lichthof","Löse die Aufgabe auf dem Blatt!", new String[]{"1001011101010100"}, "Die Zeile mit Benno muss nicht berücksichtigt werden."));
                questionMap.put(5, new Question("Cyberattacken", "Beim Treppenhaus (EG)","Wie lautet das Lösungswort?", new String[]{"Geschafft"}, "Brauchst du nicht... Denk nach!"));
                questionMap.put(6, new Question("Big Data", "Beim Treppenhaus (EG)","Max ist auf der Suche nach einem neuen Film. Welcher könnte ihm gefallen? Die Anfangsbuchstaben von einem der vier Namen kombiniert mit den beiden Filmen (als Buchstabe), die Max gefallen könnten, ergibt den Namen eines Produzenten, der Filme herstellt, welche Max ebenfalls gefallen könnte.", new String[]{"Marvel"}, "Die zwei Buchstaben, welche die beiden Filme repräsentieren, die Max gefallen könnten gehören zu den letzten beiden Buchstaben des Wortes."));
                questionMap.put(7, new Question("Rechnerarchitektur", "Heutiges Informatikzimmer","Finde die drei 7-stelligen Binärzahlen, die jeweils einzeln eine der LED's anzünden. Jede dieser 7-stelligen Binärzahlen muss dann mit der ASCII-Tabelle in ein Buchstaben umgewandelt werden. Das Lösungswort setzt sich aus den drei Buchstaben zusammen.", new String[]{"tod"}, "Probiere von den LED's zurück zu arbeiten."));
                questionMap.put(8, new Question("Verschlüsselung", "Tür zum Innenhof (Neben FS-Zimmer Musik)","Was ist der Klartext dieser Verschlüsselung?", new String[]{"Klabautermann"}, "Binärzahl in Zahl umwandeln, um in der Tabelle zu schauen"));
                break;

        }


    }

    public static void addEncryptedKeysToMap(){
        encryptionMap.put("XF98EG", 1);
        encryptionMap.put("HJ34IK", 2);
        encryptionMap.put("FQ90BM", 3);
        encryptionMap.put("KL73CS", 4);
        encryptionMap.put("QH17BX", 5);
        encryptionMap.put("EI45KS", 6);
        encryptionMap.put("TU34PC", 7);
        encryptionMap.put("AW91LD", 8);
        encryptionMap.put("GI04ZI", 9);
        encryptionMap.put("LQ61VK", 10);
        encryptionMap.put("ZU92MX", 11);
        encryptionMap.put("SE70MV", 12);
    }

    public static Question getQuestionByEncryptedKey(String encryptedKey){
        return questionMap.get(encryptionMap.get(encryptedKey));
    }

    public static int getQuestionNumberByEncryptedKey(String encryptedKey){
        return encryptionMap.get(encryptedKey);
    }

    public static String getHintByQuestionNumber(int number){
        return questionMap.get(number).getHint();
    }

    public static Question getQuestionByNumber(int number){
        return questionMap.get(number);
    }

    public static int getTotalNumberOfQuestions(){
        return questionMap.size();
    }

}
