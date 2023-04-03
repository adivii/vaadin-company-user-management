/**
 * Encoding based on Guitar Chord finger position
 * 
 * @author adivii (based on idea by _xxycin)
 */

package com.adivii.companymanagement.data.service.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuitarChordEncoder {
    // Original idea by _xxycin
    
    // Shift Scheme
    private static final List<Integer> CHORD_DM = new ArrayList<>(Arrays.asList(6, 4, 5));
    private static final List<Integer> CHORD_C = new ArrayList<>(Arrays.asList(5, 3, 2));
    private static final List<Integer> CHORD_E = new ArrayList<>(Arrays.asList(4, 2, 3));
    private static final List<Integer> CHORD_A = new ArrayList<>(Arrays.asList(3, 4, 5));
    private static final List<Integer> CHORD_D = new ArrayList<>(Arrays.asList(4, 6, 5));
    private static final List<Integer> CHORD_G = new ArrayList<>(Arrays.asList(2, 1, 6));
    private static final List<Integer> CHORD_F = new ArrayList<>(Arrays.asList(5, 4, 3));
    private static final List<Integer> CHORD_G_ALT = new ArrayList<>(Arrays.asList(2, 1, 5, 6));
    private static final List<Integer> CHORD_EM = new ArrayList<>(Arrays.asList(2, 3));
    private static final List<Integer> CHORD_AM = new ArrayList<>(Arrays.asList(5, 3, 4));

    private static List<Integer> getScheme(int index) {
        List<Integer> scheme = new ArrayList<>();

        switch (index) {
            case 0:
                scheme = CHORD_DM;
                break;
            case 1:
                scheme = CHORD_C;
                break;
            case 2:
                scheme = CHORD_E;
                break;
            case 3:
                scheme = CHORD_A;
                break;
            case 4:
                scheme = CHORD_D;
                break;
            case 5:
                scheme = CHORD_G;
                break;
            case 6:
                scheme = CHORD_F;
                break;
            case 7:
                scheme = CHORD_G_ALT;
                break;
            case 8:
                scheme = CHORD_EM;
                break;
            case 9:
                scheme = CHORD_AM;
                break;
        
            default:
                break;
        }
        
        return scheme;
    }

    public static String shiftPassword(CharSequence rawPassword) {
        List<Integer> scheme = getScheme((CustomBase64Encoder.indexOf(rawPassword.charAt(0)) + CustomBase64Encoder.indexOf(rawPassword.charAt(rawPassword.length() - 1))) % 10);
        String encodedPassword = CustomBase64Encoder.encode(rawPassword);
        String result = "";

        for(int i = 0;i < encodedPassword.length();i++) {
            result = result.concat(Character
                                        .toString(convert(encodedPassword.charAt(i),
                                                    i + 1,
                                                    scheme.get(i % scheme.size()))));
        }

        return result;
    }

    private static char convert(char ch, int index, int fingerPos) {
        return CustomBase64Encoder.charAt(CustomBase64Encoder.indexOf(ch) + Math.round((float) Math.pow(-1, index))*fingerPos);
    }
}