package com.adivii.companymanagement.views.component.dialog;

import javax.servlet.http.HttpSession;

import com.adivii.companymanagement.data.entity.Account;
import com.adivii.companymanagement.data.service.AccountService;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.NotificationService;
import com.adivii.companymanagement.data.service.PasswordValidatorService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.security.CustomPasswordEncoder;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class NewPasswordDialog extends Dialog {
    HttpSession session;

    public NewPasswordDialog(AccountService accountService, Account account) {
        session = SessionService.getCurrentSession();

        VerticalLayout dialogLayout = new VerticalLayout();
        VerticalLayout errorMessage = new VerticalLayout();
        PasswordField inputOld = new PasswordField("Old Password");
        PasswordField inputPass = new PasswordField("New Password");
        PasswordField inputRePass = new PasswordField("Confirm Password");

        inputOld.setWidthFull();
        inputPass.setWidthFull();
        inputRePass.setWidthFull();

        Button btnSave = new Button("Save");
        btnSave.setEnabled(false);
        btnSave.setWidth("50%");
        Button btnCancel = new Button("Cancel");
        btnCancel.setWidth("50%");
        HorizontalLayout btnLayout = new HorizontalLayout(btnSave, btnCancel);
        btnLayout.setWidthFull();

        inputPass.addValueChangeListener(valueChange -> {
            errorMessage.removeAll();

            if (PasswordValidatorService.matches(inputPass.getValue(), inputRePass.getValue())
                    && PasswordValidatorService.validatePassword(inputPass.getValue())) {
                btnSave.setEnabled(true);
            } else {
                btnSave.setEnabled(false);

                if (!PasswordValidatorService.matches(inputPass.getValue(), inputRePass.getValue())) {
                    errorMessage.add(new Span("Password Doesn't Match!"));
                }
                if (!PasswordValidatorService.haveLowercase(inputPass.getValue())) {
                    errorMessage.add(new Span("Must Contain Lowercase Letter"));
                }
                if (!PasswordValidatorService.haveUppercase(inputPass.getValue())) {
                    errorMessage.add(new Span("Must Contain Uppercase Letter"));
                }
                if (!PasswordValidatorService.haveNumeric(inputPass.getValue())) {
                    errorMessage.add(new Span("Must Contain Numeric Character"));
                }
                if (!PasswordValidatorService.haveSymbol(inputPass.getValue())) {
                    errorMessage.add(new Span("Must Contain Symbol"));
                }
            }
        });
        inputRePass.addValueChangeListener(valueChange -> {
            errorMessage.removeAll();

            if (PasswordValidatorService.matches(inputPass.getValue(), inputRePass.getValue())
                    && PasswordValidatorService.validatePassword(inputPass.getValue())) {
                btnSave.setEnabled(true);
            } else {
                btnSave.setEnabled(false);

                if (!PasswordValidatorService.matches(inputPass.getValue(), inputRePass.getValue())) {
                    errorMessage.add(new Span("Password Doesn't Match!"));
                }
                if (!PasswordValidatorService.haveLowercase(inputPass.getValue())) {
                    errorMessage.add(new Span("Must Contain Lowercase Letter"));
                }
                if (!PasswordValidatorService.haveUppercase(inputPass.getValue())) {
                    errorMessage.add(new Span("Must Contain Uppercase Letter"));
                }
                if (!PasswordValidatorService.haveNumeric(inputPass.getValue())) {
                    errorMessage.add(new Span("Must Contain Numeric Character"));
                }
                if (!PasswordValidatorService.haveSymbol(inputPass.getValue())) {
                    errorMessage.add(new Span("Must Contain Symbol"));
                }
            }
        });
        inputPass.setValueChangeMode(ValueChangeMode.EAGER);
        inputRePass.setValueChangeMode(ValueChangeMode.EAGER);

        // TODO: Configure saving method to save both Account and User
        btnSave.addClickListener(e -> {
            Account newAccount = account;

            if ((new CustomPasswordEncoder()).matches(inputOld.getValue(), account.getPassword())
                    && PasswordValidatorService.validatePassword(inputPass.getValue())
                    && PasswordValidatorService.matches(inputPass.getValue(), inputRePass.getValue())) {
                newAccount.setPassword((new CustomPasswordEncoder()).encode(inputPass.getValue()));

                ErrorService errorService = accountService.update(newAccount);
                if (!errorService.isErrorStatus()) {
                    close();
                } else {
                    NotificationService.showNotification(NotificationVariant.LUMO_ERROR,
                            errorService.getErrorMessage());
                }
            } else {
                if (!(new CustomPasswordEncoder()).matches(inputOld.getValue(), account.getPassword())) {
                    NotificationService.showNotification(NotificationVariant.LUMO_ERROR, "Invalid Old Password!");
                } else if (!PasswordValidatorService.validatePassword(inputPass.getValue())) {
                    NotificationService.showNotification(NotificationVariant.LUMO_ERROR, "Invalid New Password");
                } else if (!PasswordValidatorService.matches(inputPass.getValue(), inputRePass.getValue())) {
                    NotificationService.showNotification(NotificationVariant.LUMO_ERROR, "Password Doesn't Match");
                }
            }

        });

        btnCancel.addClickListener(e -> {
            close();
        });

        dialogLayout.setWidth("300px");
        dialogLayout.add(inputOld, inputPass, inputRePass, errorMessage, btnLayout);

        add(dialogLayout);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
    }
}
