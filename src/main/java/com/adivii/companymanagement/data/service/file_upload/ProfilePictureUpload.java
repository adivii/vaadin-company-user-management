package com.adivii.companymanagement.data.service.file_upload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;

import com.adivii.companymanagement.data.service.security.CustomBase64Encoder;
import com.adivii.companymanagement.data.service.security.GuitarChordEncoder;

// TODO: Make sure user can select which part of image to upload
public class ProfilePictureUpload {
    public static String generateProfilePictureTitle(String firstName, String lastName) {
        String firstPart, lastPart;

        if (firstName.length() > 5) {
            firstPart = firstName.substring(0, 5);
        } else {
            firstPart = firstName;
        }

        if (lastName.length() > 5) {
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
            // Use it if server use UNIX system
            // Files.setPosixFilePermissions(dest.toPath(), perms);

            BufferedImage sourceImage = ImageIO.read(dest);
            sourceImage = preprocessImage(sourceImage);
            ImageIO.write(sourceImage, "jpg", dest);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void saveFile(InputStream source, String filename) {
        File dest = new File(getFolder().concat(filename));
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);

        try {
            FileUtils.copyInputStreamToFile(source, dest);
            // Files.setPosixFilePermissions(dest.toPath(), perms);

            BufferedImage sourceImage = ImageIO.read(dest);
            sourceImage = preprocessImage(sourceImage);
            ImageIO.write(sourceImage, "jpg", dest);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static BufferedImage preprocessImage(BufferedImage sourceImage) {
        BufferedImage finalImage = sourceImage;

        if (finalImage.getWidth() > finalImage.getHeight()) {
            finalImage = Scalr.resize(finalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_HEIGHT, 600);
            finalImage = finalImage.getSubimage((finalImage.getWidth() / 2) - (finalImage.getHeight() / 2), 0,
                    finalImage.getHeight(), finalImage.getHeight());
        } else {
            finalImage = Scalr.resize(finalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, 600);
            finalImage = finalImage.getSubimage(0, (finalImage.getHeight() / 2) - (finalImage.getWidth() / 2),
                    finalImage.getWidth(), finalImage.getWidth());
        }

        return finalImage;
    }

    public static String getFolder() {
        return "E:\\Apps\\xampp\\htdocs\\vaadin-crm-resource\\profiles\\";
    }

    public static String getLink() {
        return "http://localhost/vaadin-crm-resource/profiles/";
    }
}
