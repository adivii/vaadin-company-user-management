package com.adivii.companymanagement.data.service.generator;

import java.util.ArrayList;
import java.util.Random;

public class IdGeneratorService {
    // TODO: Fix the integration with database and authentication method, stil causing error when login
    private final static String LOWERCASE_LETTER = "qwertyuiopasdfghjklzxcvbnm";
    private final static String UPPERCASE_LETTER = "QWERTYUIOPASDFGHJKLZXCVBNM";
    private final static String NUMBER = "1234567890";

    public static String generateID() {
        ArrayList<String> sections = new ArrayList<>();
        for(int i = 0;i < 8;i++) {
            sections.add(generateSection());
        }        

        return String.join("-", sections);
    }

    private static String generateSection() {
        Random rnd = new Random();
        String pool = LOWERCASE_LETTER.concat(UPPERCASE_LETTER).concat(NUMBER);
        String section = "";

        for(int i = 0;i < 6;i++) {
            section = section.concat(Character.toString(pool.charAt(rnd.nextInt(pool.length()))));
        }

        return section;
    }
}
