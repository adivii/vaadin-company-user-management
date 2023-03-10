package com.adivii.companymanagement.views.dialog;

import javax.servlet.http.HttpSession;

import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.security.CustomPasswordEncoder;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;

public class NewPasswordDialog extends Dialog {
    HttpSession session;

    public NewPasswordDialog(UserService userService) {
        session = SessionService.getCurrentSession();

        VerticalLayout dialogLayout = new VerticalLayout();
        PasswordField inputOld = new PasswordField("Old Password");
        PasswordField inputPass = new PasswordField("New Password");
        PasswordField inputRePass = new PasswordField("Confirm Password");
        
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel");
        HorizontalLayout btnLayout = new HorizontalLayout(btnSave, btnCancel);

        // TODO: Add checker for inputPass and inputRePass similiarity
        // TODO: Add checker for old pass validity
        btnSave.addClickListener(e -> {
            User user = userService.getUser((Integer) session.getAttribute("userID")).get();
            user.setPassword((new CustomPasswordEncoder()).encode(inputPass.getValue()));
            user.setActivated(true);
            
            ErrorService errorService = userService.editData(user);
            if (!errorService.isErrorStatus()) {
                close();
            } else {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text(errorService.getErrorMessage());
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();
            }
        });

        btnCancel.addClickListener(e -> {
            close();
        });

        dialogLayout.add(inputOld, inputPass, inputRePass, btnLayout);

        add(dialogLayout);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
    }
}
