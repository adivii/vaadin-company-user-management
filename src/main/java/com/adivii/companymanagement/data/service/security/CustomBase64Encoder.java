/**
 * Custom Base64 Encoding Scheme
 * Encode a given String (array of 8-bit character) into a sequence
 * of 6-bit character.
 * 
 * @author adivii
 */
package com.adivii.companymanagement.data.service.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * CustomBase64Encoder
 */
public class CustomBase64Encoder {
    /**
     * Conversion Table Used for Int-to-Char conversion
     * We use indexOf() and charAt() to do the conversion
     */
    public final static List<Character> CHAR_CONVERSION_TABLE = Arrays.asList(
       'q', 'w', 'e', 'r', 't', 'y', 'u', 'i',
       'o', 'p', 'a', 's', 'd', 'f', 'g', 'h',
       'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b',
       'n', 'm', 'Q', 'W', 'E', 'R', 'T', 'Y',
       'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F',
       'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C',
       'V', 'B', 'N', 'M', '0', '1', '2', '3',
       '4', '5', '6', '7', '8', '9', '-', '_'
    );

    public static String encode(CharSequence rawString) {
        String result = "";
        List<String> binarySequence = splitSequence(getBinarySequence(rawString, 8), 6);

        for (String sequence : binarySequence) {
            result = result.concat(Character.toString(charAt(Integer.parseInt(sequence, 2))));
        }

        if(result.length() % 4 != 0) {
            result = result.concat("=".repeat(4 - (result.length() % 4)));
        }

        return result;
    }

    public static String decode(CharSequence encodedString) {
        String result = "";

        // Remove padding character (~)
        String rawString = encodedString.toString();
        Pattern pattern = Pattern.compile("=*");
        rawString = pattern.matcher(rawString).replaceAll("");

        List<String> binarySequence = splitSequence(getBinarySequenceDecode(rawString, 6), 8);

        for (String sequence : binarySequence) {
            if(Integer.parseInt(sequence, 2) == 0){
                continue;
            }
            
            result = result.concat(Character.toString((char) (Integer.parseInt(sequence, 2))));
        }

        return result;
    }

    private static String getBinarySequence(CharSequence rawString, int sequenceLength) {
        String result = "";

        for(int i = 0;i < rawString.length();i++) {
            String temp = Integer.toBinaryString((int) rawString.charAt(i));

            result = result.concat("0".repeat(sequenceLength-temp.length()).concat(temp));
        }

        return result;
    }

    private static String getBinarySequenceDecode(CharSequence rawString, int sequenceLength) {
        String result = "";

        for(int i = 0;i < rawString.length();i++) {
            String temp = Integer.toBinaryString(indexOf(rawString.charAt(i)));

            result = result.concat("0".repeat(sequenceLength-temp.length()).concat(temp));
        }

        return result;
    }

    private static List<String> splitSequence(CharSequence binarySequence, int sequenceLength) {
        List<String> chunks = new ArrayList<>();

        String temp;

        if(binarySequence.length() % sequenceLength != 0){
            temp = binarySequence.toString().concat("0".repeat(sequenceLength - (binarySequence.length() % sequenceLength)));
        }else{
            temp = binarySequence.toString();
        }

        for (int i = 0; i < temp.length(); i += sequenceLength) {
            chunks.add(temp.toString().substring(i, Math.min(temp.length(), i + sequenceLength)));
        }

        return chunks;
    }

    public static int indexOf(Character ch) {
        return CHAR_CONVERSION_TABLE.indexOf(ch);
    }

    public static Character charAt(int index) {
        if(index < 0){
            return CHAR_CONVERSION_TABLE.get(CHAR_CONVERSION_TABLE.size() + index);
        }else if(index >= CHAR_CONVERSION_TABLE.size()){
            return CHAR_CONVERSION_TABLE.get(0 + (index - CHAR_CONVERSION_TABLE.size()));
        }else{
            return CHAR_CONVERSION_TABLE.get(index);
        }
        
    }
}