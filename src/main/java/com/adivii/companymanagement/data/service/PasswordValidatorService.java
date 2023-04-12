package com.adivii.companymanagement.data.service;

import java.util.regex.Pattern;

public class PasswordValidatorService {

    public static boolean validatePassword(String password) {
        return (haveLowercase(password) && haveUppercase(password)
                && haveNumeric(password) && haveSymbol(password));
    }

    public static boolean matches(String password, String confirmationPassword) {
        return password.equals(confirmationPassword);
    }

    public static boolean haveLowercase(String password) {
        Pattern patternLowercase = Pattern.compile("[a-zA-Z0-9]*[a-z]+[a-zA-Z0-9]*");

        return patternLowercase.matcher(password).find();
    }

    public static boolean haveUppercase(String password) {
        Pattern patternUppercase = Pattern.compile("[a-zA-Z0-9]*[A-Z]+[a-zA-Z0-9]*");

        return patternUppercase.matcher(password).find();
    }

    public static boolean haveNumeric(String password) {
        Pattern patternNumeric = Pattern.compile("[a-zA-Z0-9]*[0-9]+[a-zA-Z0-9]*");

        return patternNumeric.matcher(password).find();
    }

    public static boolean haveSymbol(String password) {
        Pattern patternSymbol = Pattern.compile("[a-zA-Z0-9]*[~`&!@():-]+[a-zA-Z0-9]*");

        return patternSymbol.matcher(password).find();
    }
}