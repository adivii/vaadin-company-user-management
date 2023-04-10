package com.adivii.companymanagement.views;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

import com.adivii.companymanagement.data.entity.Avatar;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.AccountService;
import com.adivii.companymanagement.data.service.AvatarService;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.DepartmentService;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.file_upload.ProfilePictureUpload;
import com.adivii.companymanagement.views.component.CustomAvatar;
import com.adivii.companymanagement.views.component.CustomUploadButton;
import com.adivii.companymanagement.views.component.dialog.NewPasswordDialog;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import de.f0rce.cropper.Cropper;
import de.f0rce.cropper.settings.CropperSettings;
import de.f0rce.cropper.settings.enums.ViewMode;

@Route("/setting")
@PageTitle("User Setting")
public class UserSetting extends HorizontalLayout {
    private UserService userService;
    private DepartmentService departmentService;
    private CompanyService companyService;
    private AccountService accountService;
    private AvatarService avatarService;
    private HttpSession session;

    public UserSetting(UserService userService, DepartmentService departmentService, CompanyService companyService,
            AccountService accountService, AvatarService avatarService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.companyService = companyService;
        this.avatarService = avatarService;
        this.accountService = accountService;
        this.session = SessionService.getCurrentSession();

        VerticalLayout sidebar = new SidebarLayout(this.userService);
        UserSettingMainLayout mainLayout = new UserSettingMainLayout(this.userService, this.avatarService,
                accountService);
        Scroller scroller = new Scroller(mainLayout.getLayout());

        sidebar.setWidth("250px");

        setSizeFull();
        add(sidebar, scroller);
    }
}

class UserSettingMainLayout extends VerticalLayout {
    VerticalLayout layout;
    UserService userService;
    private AvatarService avatarService;
    private AccountService accountService;
    HttpSession session;

    User user;

    // Layout for message
    Span message;

    // Layout for name
    TextField inputFirst;
    TextField inputLast;
    HorizontalLayout nameLayout;

    // Layout for address
    TextArea inputAddress;

    // Layout for phone number
    TextField inputPhone;

    // Layout for profile picture upload
    CustomAvatar profilePicture;
    InputStream inputProfileStream;
    MemoryBuffer fileBuffer;
    CustomUploadButton inputProfilePict;
    File lastUploadedFile;

    // Layout for button
    Button btnSave;
    Button btnChangePass;
    HorizontalLayout buttonLayout;

    public UserSettingMainLayout(UserService userService, AvatarService avatarService, AccountService accountService) {
        this.userService = userService;
        this.avatarService = avatarService;
        this.accountService = accountService;
        this.session = SessionService.getCurrentSession();

        this.layout = new VerticalLayout();
        this.user = userService.getUser((Integer) session.getAttribute("userID")).get();

        this.message = new Span();

        this.inputFirst = new TextField("First Name");
        this.inputLast = new TextField("Last Name");
        this.nameLayout = new HorizontalLayout(this.inputFirst, this.inputLast);

        this.inputAddress = new TextArea("Address");

        this.inputPhone = new TextField("Phone Number");
        new PhoneI18nFieldFormatter(PhoneI18nFieldFormatter.REGION_ID).extend(this.inputPhone);

        // TODO: Fix logic for profile auto-update
        this.profilePicture = new CustomAvatar(user.getFirstName().concat(" ").concat(user.getLastName()));
        this.profilePicture.setColor(((int) user.getFirstName().charAt(0) + (int) user.getLastName().charAt(0)) % 4);
        this.profilePicture.setSize("100px");

        this.fileBuffer = new MemoryBuffer();
        inputProfilePict = new CustomUploadButton(fileBuffer);
        inputProfilePict.setMaxFileSize(10 * 1048576); // 10 MB

        // Set up cropper component
        // cropper = new Cropper(profilePicture);
        // cropper.withAspectRatio(1, 1);
        // cropper.withPreviewSize(200, 200);
        // cropper.withOutputSize(100, 100);
        // add(cropper);

        this.btnSave = new Button("Save");
        this.btnChangePass = new Button("Change Password");
        this.buttonLayout = new HorizontalLayout(this.btnSave);

        this.updateField();
    }

    private void updateField() {

        this.user = userService.getUser((Integer) session.getAttribute("userID")).get();

        // Setting for name field
        inputFirst.setValue(user.getFirstName());
        inputLast.setValue(user.getLastName());

        // Setting for address
        inputAddress.setWidthFull();
        inputAddress.setMaxLength(255);
        inputAddress.setHelperText("0/255");

        inputAddress.setValueChangeMode(ValueChangeMode.EAGER);
        inputAddress.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/255");
        });

        inputAddress.setValue(user.getAddress());

        // Setting for phone number
        new PhoneI18nFieldFormatter(PhoneI18nFieldFormatter.REGION_ID).extend(inputPhone);
        inputPhone.setWidthFull();

        inputPhone.setValue(user.getPhoneNumber());

        // Setting for profile picture
        // TODO: Make sure user can select which part of image to upload
        if (user.getAvatar() != null) {
            User temp = user;
            this.profilePicture.setAvatar(new Image(new StreamResource("profile", () -> {
                InputStream profileStream;
                try {
                    profileStream = new URL(user.getAvatar().getUri()).openStream();
                    return profileStream;
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return null;
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return null;
                }
            }), null));
        }

        inputProfilePict.addSucceededListener(e -> {
            InputStream profileStream = fileBuffer.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            inputProfileStream = profileStream;

            try {
                ImageIO.write(ProfilePictureUpload.preprocessImage(ImageIO.read(profileStream)), "png", os);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            Dialog dialog = new Dialog();
            dialog.setHeight("600px");
            dialog.setWidth("1050px");
            dialog.setCloseOnOutsideClick(false);
            this.addToDialog(os, dialog, e.getMIMEType());

            // this.profilePicture.setAvatar(new Image(new StreamResource("temp_photo", ()
            // -> {
            // try {
            // BufferedImage bim =
            // ProfilePictureUpload.preprocessImage(ImageIO.read(profileStream));
            // ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // ImageIO.write(bim, "png", bos);
            // return new ByteArrayInputStream(bos.toByteArray());
            // } catch (IOException e1) {
            // // TODO Auto-generated catch block
            // e1.printStackTrace();
            // return null;
            // }
            // }), null));
        });

        // Setting for button
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // TODO: Save button doen't do anything
        btnSave.addClickListener(e -> {
            ProfilePictureUpload.saveFile(inputProfileStream,
                    ProfilePictureUpload.generateProfilePictureTitle(user.getEmail()));
            Avatar newAvatar;
            if (user.getAvatar() == null) {
                newAvatar = new Avatar();
            } else {
                newAvatar = user.getAvatar();
            }

            newAvatar.setUri(ProfilePictureUpload.getLink()
                    .concat(ProfilePictureUpload.generateProfilePictureTitle(user.getEmail())));

            this.avatarService.saveAvatar(newAvatar);

            user.setFirstName(inputFirst.getValue());
            user.setLastName(inputLast.getValue());
            user.setAddress(inputAddress.getValue());
            user.setPhoneNumber(inputPhone.getValue());
            user.setAvatar(avatarService.searchByUri(newAvatar.getUri()));

            ErrorService error = userService.editData(user);
            if (error.isErrorStatus()) {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text(error.getErrorMessage());
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();
            } else {
                this.updateField();

                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                Text notificationText = new Text("Success");
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();
            }
        });

        btnChangePass.setEnabled(true);
        btnChangePass.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnChangePass.addClickListener(e -> {
            NewPasswordDialog passwordDialog = new NewPasswordDialog(this.accountService, user.getAccount());

            passwordDialog.open();
        });
    }

    public VerticalLayout getLayout() {
        return new VerticalLayout(this.message,
                this.nameLayout,
                this.inputAddress,
                this.inputPhone,
                this.btnChangePass,
                this.profilePicture,
                this.inputProfilePict,
                this.buttonLayout);
    }

    public void addToDialog(ByteArrayOutputStream os, Dialog dialog, String mimeType) {
        Button cropButton = new Button("Crop");

        CropperSettings croppersett = new CropperSettings();
        croppersett.setAspectRatio(1);
        croppersett.setViewMode(ViewMode.ONE);
        croppersett.setCroppedImageHeight(250);
        croppersett.setCroppedImageHeight(250);
        croppersett.setRoundCropBox(true);

        Cropper crop = new Cropper(
                croppersett, java.util.Base64.getEncoder().encodeToString(os.toByteArray()), mimeType);
        crop.setHeight("500px");
        crop.setWidth("1000px");
        crop.setEncoderQuality(1.00);

        cropButton.addClickListener(
                event -> {
                    dialog.close();
                    String imageUri = crop.getImageUri();
                    inputProfileStream = new ByteArrayInputStream(crop.getImageBase64());
                    Image img = new Image();
                    img.setSrc(imageUri);
                    this.profilePicture.setAvatar(img);
                });

        dialog.add(crop, cropButton);
        dialog.open();
    }
}