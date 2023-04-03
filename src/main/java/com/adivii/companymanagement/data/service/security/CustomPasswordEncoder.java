package com.adivii.companymanagement.data.service.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Used to build encryption
// TODO: User random method to build encryption (either use data like firstname, lastname, email, etc)
// For sake of experiment, use Huffman Compression method
// Change of idea, we use Osmy's idea to make encryption
@Service
@Transactional
public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return encode(rawPassword.toString(), getStringSalt());
    }

    public String encode(String password, String salt) {
        String generatedPassword = "";
        String shiftedPassword = GuitarChordEncoder.shiftPassword(password);

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(shiftedPassword.getBytes(StandardCharsets.UTF_8));

            // Convert Byte to String
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                // TODO: Find out usage of Two's Complement (?) here
                // TODO: Find out usage of substring here
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return generatedPassword.concat("$").concat(salt);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String salt = encodedPassword.substring(encodedPassword.indexOf("$") + 1);

        return encodedPassword.equals(encode(rawPassword.toString(), salt));
    }
    
    public String getStringSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt.toString();
    }
}
