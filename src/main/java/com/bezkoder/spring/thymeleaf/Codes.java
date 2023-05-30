package com.bezkoder.spring.thymeleaf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

public class Codes {
    private static final int LIST_SIZE = 10000;
    private static final int CODE_LENGTH = 4;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789";
    private static List<String> codeList;
    private static int index = 0;

    static {
        Set<String> codes = new HashSet<>();
        Random random = new Random();

        while (codes.size() < LIST_SIZE) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < CODE_LENGTH; j++) {
                int randomIndex = random.nextInt(CHARACTERS.length());
                char randomChar = CHARACTERS.charAt(randomIndex);
                stringBuilder.append(randomChar);
            }
            codes.add(stringBuilder.toString());
        }

        codeList = new ArrayList<>(codes);

    }

    public static String getNewCode(){
        String code = codeList.get(index);
        index++;
        return code;
    }

    public static void printCodes(){
        for (String s : codeList){
            System.out.println(s);
        }
    }

    public static void resetIndex(){
        index = 0;
    }

}