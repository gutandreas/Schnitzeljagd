package com.bezkoder.spring.thymeleaf;

import java.util.HashMap;
import java.util.LinkedList;

public class QuestionList {

    private static final HashMap<Integer, Question> questionMap = new HashMap<>();
    private static final HashMap<String, Integer> encryptionMap = new HashMap<>();
    static {
        questionMap.put(1, new Question("turnhalle","Wie heisst Einstein mit Vorname?", new String[]{"Albert"}, "Er beginnt mit 'A'", "Gehe zum Haupteingang"));
        questionMap.put(2, new Question("haupteingang", "Wie heisst der Rektor mit Vorname?", new String[]{"Ueli", "Ulrich"}, "Er beginnt mit 'U'", "Gehe zum Sekretariat"));
        questionMap.put(3, new Question("sekretariat","Wie heisst Bundesrat Berset mit Vorname", new String[]{"Alain"}, "Er beginnt mit 'A'", "Gehe zur Aula"));

        encryptionMap.put("XF98EG", 1);
        encryptionMap.put("HJ34IK", 2);
        encryptionMap.put("FQ90BM", 3);

    }

    public static Question getQuestionByEncryptedKey(String encryptedKey){
        return questionMap.get(encryptionMap.get(encryptedKey));
    }

    public static int getQuestionNumberByEncryptedKey(String encryptedKey){
        return encryptionMap.get(encryptedKey);
    }

    public static String getQuestionHintByKey(String key){
        return questionMap.get(key).getHint();
    }
}
