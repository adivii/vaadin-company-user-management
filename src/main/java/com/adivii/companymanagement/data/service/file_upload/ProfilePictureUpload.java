package com.adivii.companymanagement.data.service.file_upload;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

import de.f0rce.cropper.Cropper;
import de.f0rce.cropper.settings.CropperSettings;
import de.f0rce.cropper.settings.enums.ViewMode;

// TODO: Make sure user can select which part of image to upload
public class ProfilePictureUpload {
    public static String generateProfilePictureTitle(String email) {
        // String temp = email.substring(0, email.indexOf("@") + 1);
        return CustomBase64Encoder.encode(email.substring(0, email.indexOf("@")));
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
        } else {
            finalImage = Scalr.resize(finalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, 600);
        }

        return finalImage;
    }

    // public static BufferedImage getCroppedImage(ByteArrayOutputStream os, Dialog dialog, String mimeType) {
    //     Button cropButton = new Button("Crop");
    
    //     CropperSettings croppersett = new CropperSettings();
    //     croppersett.setAspectRatio(1);
    //     croppersett.setViewMode(ViewMode.ONE);
    //     croppersett.setCroppedImageHeight(250);
    //     croppersett.setCroppedImageHeight(250);
    //     croppersett.setRoundCropBox(true);
    
    //     Cropper crop =
    //         new Cropper(
    //             croppersett, java.util.Base64.getEncoder().encodeToString(os.toByteArray()), mimeType);
    //     crop.setHeight("500px");
    //     crop.setWidth("1000px");
    //     crop.setEncoderQuality(1.00);
    
    //     cropButton.addClickListener(
    //         event -> {
    //           dialog.close();
    //           String imageUri = crop.getImageUri();
    //           BufferedImage image = ImageIO.read(new File(imageUri));
    //         });
    
    //     dialog.add(crop, cropButton);
    //     dialog.open();
    //   }

    public static String getFolder() {
        return "E:\\Apps\\xampp\\htdocs\\vaadin-crm-resource\\profiles\\";
    }

    public static String getLink() {
        return "http://localhost/vaadin-crm-resource/profiles/";
    }
}
