package com.adivii.companymanagement.data.service.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.service.SessionService;

// Used to build encryption
// TODO: User random method to build encryption (either use data like firstname, lastname, email, etc)
// For sake of experiment, use Huffman Compression method
// Change of idea, we use Osmy's idea to make encryption
@Service
public class CustomPasswordEncoder implements PasswordEncoder {
    private final List<Integer> CHORD_DM = new ArrayList<>(Arrays.asList(6, 4, 5));
    private final List<Integer> CHORD_C = new ArrayList<>(Arrays.asList(5, 3, 2));
    private final List<Integer> CHORD_E = new ArrayList<>(Arrays.asList(4, 2, 3));
    private final List<Integer> CHORD_A = new ArrayList<>(Arrays.asList(3, 4, 5));
    private final List<Integer> CHORD_D = new ArrayList<>(Arrays.asList(4, 6, 5));
    private final List<Integer> CHORD_G = new ArrayList<>(Arrays.asList(2, 1, 6));
    private final List<Integer> CHORD_F = new ArrayList<>(Arrays.asList(5, 4, 3));
    private final List<Integer> CHORD_G_ALT = new ArrayList<>(Arrays.asList(2, 1, 5, 6));
    private final List<Integer> CHORD_EM = new ArrayList<>(Arrays.asList(2, 3));
    private final List<Integer> CHORD_AM = new ArrayList<>(Arrays.asList(5, 3, 4));

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public CustomPasswordEncoder() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    // Credit for the original idea by _xxycin
    @Override
    public String encode(CharSequence rawPassword) {
        String generatedPass = generatePassword(rawPassword.toString());
        String finalPass = this.bCryptPasswordEncoder.encode(generatedPass);
        return finalPass;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return this.bCryptPasswordEncoder.matches(this.generatePassword(rawPassword.toString()), encodedPassword);
    }
    
    private char convert(char ch, int index, int fingerPos) {
        return (char) (((int) ch) + Math.pow(-1, index)*fingerPos);
    }

    private String generatePassword(String pass) {
        String result = new String();
        List<Integer> pattern = new ArrayList<>();

        String schemeCode = new String();

        int additionOfFirstAndLast = (int) pass.charAt(0) + (int) pass.charAt(pass.length() - 1);
        int lastDigit = additionOfFirstAndLast % 10;

        String saltedPass = pass.substring(0, 2) + pass.substring(pass.length() - 2, pass.length()) + pass;

        switch (lastDigit) {
            case 0:
                pattern = this.CHORD_DM;
                schemeCode = "$DM";
                break;
            case 1:
                pattern = this.CHORD_C;
                schemeCode = "$CO";
                break;
            case 2:
                pattern = this.CHORD_E;
                schemeCode = "$EO";
                break;
            case 3:
                pattern = this.CHORD_A;
                schemeCode = "$AO";
                break;
            case 4:
                pattern = this.CHORD_D;
                schemeCode = "$DO";
                break;
            case 5:
                pattern = this.CHORD_G;
                schemeCode = "$GO";
                break;
            case 6:
                pattern = this.CHORD_F;
                schemeCode = "$FO";
                break;
            case 7:
                pattern = this.CHORD_G_ALT;
                schemeCode = "$GA";
                break;
            case 8:
                pattern = this.CHORD_EM;
                schemeCode = "$EM";
                break;
            case 9:
                pattern = this.CHORD_AM;
                schemeCode = "$AM";
                break;
        
            default:
                break;
        }

        for(int i = 0;i < saltedPass.length();i++) {
            result = result.concat(Character
                                        .toString(convert(saltedPass.charAt(i),
                                                    i + 1,
                                                    pattern.get(i % pattern.size()))));
        }

        return result;
    }
}
