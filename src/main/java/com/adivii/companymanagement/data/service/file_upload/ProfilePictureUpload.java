package com.adivii.companymanagement.data.service.file_upload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.adivii.companymanagement.data.service.security.CustomBase64Encoder;
import com.adivii.companymanagement.data.service.security.GuitarChordEncoder;

public class ProfilePictureUpload {
    // TODO: Fix service for profil picture upload
    public static String generateProfilePictureTitle(String firstName, String lastName) {
        String firstPart, lastPart;

        if(firstName.length() > 5) {
            firstPart = firstName.substring(0, 5);
        } else {
            firstPart = firstName;
        }

        if(lastName.length() > 5) {
            lastPart = lastName.substring(0, 5);
        } else {
            lastPart = lastName;
        }

        return CustomBase64Encoder.encode(firstPart.concat(lastPart));
    }

    public static void saveFile(File file, String filename) {
        File dest = new File(getFolder().concat(filename));
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);

        try {
            FileUtils.copyFile(file, dest, StandardCopyOption.REPLACE_EXISTING);
            Files.setPosixFilePermissions(dest.toPath(), perms);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getFolder() {
        return "/opt/lampp/htdocs/vaadin-company-management-resource/profiles/";
    }
}
