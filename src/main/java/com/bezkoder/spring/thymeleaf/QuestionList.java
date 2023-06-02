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

        switch (modus){
            case 1:
                System.out.println("Modus Kantifest gestartet");
                questionMap.put(1, new Question("Einstieg", "Zimmer 013","Diese Schnitzeljagd ist von der Fachschaft des Fachs ... organisiert.", new String[]{"Informatik"}, "Es beginnt mit 'I'"));
                questionMap.put(2, new Question("Leuchtendes Lämpchen", "Vor Bibliothek", "Wie heisst das Lösungswort, mit dem das Lämpchen leuchtet?", new String[]{"jodeln"}, "Beginne beim Schaltplan vom Lämpchen her. Ein gesuchter Buchstabe ist 'j'."));
                questionMap.put(3, new Question("Zahleneigenschaft", "Zimmer 010 (Aufgabe 'Zahleneigenschaft')","Welche Eigenschaft muss die erkannte Zahl haben, damit Musik gespielt wird?", new String[]{"primzahl", "primzahlen", "prim"}, "Es hat etwas mit der Teilbarkeit zu tun."));
                questionMap.put(4, new Question("Distanz", "Zimmer 010 (Aufgabe 'Distanz')", "Welche Eigenschaft muss die Distanz haben, damit Musik gespielt wird? (In einem Wort)", new String[]{"quadrat", "quadratzahl"}, "Das 'Gegenteil' der Wurzel..."));
                questionMap.put(5, new Question("Mimik", "Zimmer 013 (Aufgabe 'Mimik')","Was muss man tun, damit die Musik abgespielt wird?", new String[]{"lächeln", "lachen", "smile"}, "Schau nicht so böse..."));
                questionMap.put(6, new Question("Menschen 'scannen'", "Zimmer 013 (Aufgabe 'Menschen scannen')","Welche Eigenschaft muss der erkannte Mensch haben, damit Musik gespielt wird?", new String[]{"alt", "älter", "alte menschen", "greis"}, "Es ist möglich, dass Bilder aus dem Internet in die Kamera gezeigt werden müssen, weil du selbst die Eigenschaft nicht erfüllst..."));
                questionMap.put(7, new Question("Informatik-Hangman", "Zimmer 010 (Aufgabe 'Informatik-Hangman')","Frage Philippe", new String[]{"flashback"}, "Dazu ist kein Tipp nötig... Streng dich etwas mehr an... ;-)"));
                questionMap.put(8, new Question("Legend of the wild sea", "Galerie 1. Etage","Frage Philippe", new String[]{"flashback"}, "Dazu ist kein Tipp nötig... Streng dich etwas mehr an... ;-)"));
                questionMap.put(9, new Question("Anidex", "Vor dem Zimmer 212","Frage Philippe", new String[]{"flashback"}, "Dazu ist kein Tipp nötig... Streng dich etwas mehr an... ;-)"));





                break;
            case 2:
                System.out.println("Modus G21s gestartet");
                questionMap.put(1, new Question("Präsident","Turnhalle","Wie heisst Trump mit Vorname?", new String[]{"Donald"}, "Er beginnt mit 'D'"));
                questionMap.put(2, new Question("Rektor, ", "Haupteingang", "Wie heisst der Rektor mit Vorname?", new String[]{"Ueli", "Ulrich"}, "Er beginnt mit 'U'"));
                questionMap.put(3, new Question("Bundesrat", "Sekretariat","Wie heisst Bundesrat Berset mit Vorname?", new String[]{"Alain"}, "Er beginnt mit 'A'"));
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
