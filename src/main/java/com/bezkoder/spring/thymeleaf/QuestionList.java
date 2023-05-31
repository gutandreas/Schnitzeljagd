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
                questionMap.put(1, new Question("Physiker", "Turnhalle","Wie heisst der Physiker Einstein mit Vorname?", new String[]{"Albert"}, "Er beginnt mit 'A'", "Haupteingang"));
                questionMap.put(2, new Question("Rektor", "Haupteingang", "Wie heisst der Rektor mit Vorname?", new String[]{"Ueli", "Ulrich"}, "Er beginnt mit 'U'", "Sekretariat"));
                questionMap.put(3, new Question("Bundesrat", "Sekretariat","Wie heisst Bundesrat Berset mit Vorname?", new String[]{"Alain"}, "Er beginnt mit 'A'", "Aula"));
                break;
            case 2:
                System.out.println("Modus G21s gestartet");
                questionMap.put(1, new Question("Präsident","Turnhalle","Wie heisst Trump mit Vorname?", new String[]{"Donald"}, "Er beginnt mit 'D'", "Haupteingang"));
                questionMap.put(2, new Question("Rektor, ", "Haupteingang", "Wie heisst der Rektor mit Vorname?", new String[]{"Ueli", "Ulrich"}, "Er beginnt mit 'U'", "Sekretariat"));
                questionMap.put(3, new Question("Bundesrat", "Sekretariat","Wie heisst Bundesrat Berset mit Vorname?", new String[]{"Alain"}, "Er beginnt mit 'A'", "Aula"));
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
