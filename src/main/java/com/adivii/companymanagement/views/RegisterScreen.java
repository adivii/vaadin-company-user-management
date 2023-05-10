package com.adivii.companymanagement.views;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestParam;

import com.adivii.companymanagement.data.entity.Account;
import com.adivii.companymanagement.data.entity.RoleMap;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.AccountService;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.DepartmentService;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.MailSenderService;
import com.adivii.companymanagement.data.service.NotificationService;
import com.adivii.companymanagement.data.service.PasswordValidatorService;
import com.adivii.companymanagement.data.service.RoleMapService;
import com.adivii.companymanagement.data.service.RoleService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.security.CustomPasswordEncoder;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

// TODO: Handle Success and Error
// TODO: Show Notification
// TODO: Modify Scheme, so that account can exist without complete data, but user must input required data on first login

@Route("/register")
@PageTitle("Register User")
public class RegisterScreen extends VerticalLayout {

    // Services
    private UserService userService;
    private AccountService accountService;
    private CompanyService companyService;
    private DepartmentService departmentService;
    private RoleService roleService;
    private RoleMapService roleMapService;

    private JavaMailSender mailSender;

    // Account (Email and Password)
    private EmailField emailInput;
    private PasswordField passInput;
    private PasswordField rePassInput;
    private HorizontalLayout passLayout;
    // Error Message
    VerticalLayout errorMessage;
    // Button
    private Button btnSaveUser;

    public RegisterScreen(UserService userService, AccountService accountService,
            CompanyService companyService, DepartmentService departmentService, RoleService roleService,
            RoleMapService roleMapService, JavaMailSender mailSender) {
        this.userService = userService;
        this.accountService = accountService;
        this.companyService = companyService;
        this.departmentService = departmentService;
        this.roleMapService = roleMapService;
        this.roleService = roleService;
        this.mailSender = mailSender;

        setAlignItems(Alignment.CENTER);
        this.add(initiateForm());
    }

    // private void checkStatus() {
    // String emailTemp;
    // this.add(initiateForm());

    // if (this.emailList != null) {
    // emailTemp = this.emailList.get(0);
    // } else {
    // emailTemp = "";
    // }

    // if (userService.getByEmail(emailTemp).size() > 0) {
    // if (roleMapService.getByEmail(emailTemp).size() == 0) {
    // RoleMap newRoleMap = new RoleMap();
    // newRoleMap.setUser(userService.getByEmail(emailTemp).get(0));
    // roleMapService.add(newRoleMap);

    // User tempUser = userService.getByEmail(emailTemp).get(0);
    // tempUser.setActivated(false);
    // userService.editData(tempUser);
    // }
    // if (accountService.getByEmail(emailTemp).size() == 0) {
    // emailInput.setValue(emailList.get(0));
    // emailInput.setReadOnly(true);
    // btnSaveUser.addClickListener(e -> {
    // // TODO: Implement rollback scenario, if user failed to save data
    // addInvitedUser(userService.getByEmail(emailTemp).get(0));
    // });
    // } else {
    // UI.getCurrent().getPage().setLocation("/login");
    // }
    // } else {
    // btnSaveUser.addClickListener(e -> {
    // // TODO: Implement rollback scenario, if user failed to save data
    // createNewUser();
    // });
    // }
    // }

    private VerticalLayout initiateForm() {
        // Account (Email and Password)
        emailInput = new EmailField("Email Address");
        passInput = new PasswordField("Password");
        rePassInput = new PasswordField("Confirm Password");
        passLayout = new HorizontalLayout(passInput, rePassInput);

        passInput.addValueChangeListener(valueChange -> {
            errorMessage.removeAll();

            validatePasswordInput();
        });
        passInput.setValueChangeMode(ValueChangeMode.EAGER);
        rePassInput.addValueChangeListener(valueChange -> {
            errorMessage.removeAll();

            validatePasswordInput();
        });
        rePassInput.setValueChangeMode(ValueChangeMode.EAGER);

        // Button
        btnSaveUser = new Button("Save");
        btnSaveUser.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSaveUser.setEnabled(false);
        btnSaveUser.addClickListener(e -> {
            // TODO: Implement rollback scenario, if user failed to save data
            createNewUser();
        });

        errorMessage = new VerticalLayout();

        return new VerticalLayout(emailInput, passLayout, errorMessage, btnSaveUser);
    }

    private void createNewUser() {
        CustomPasswordEncoder encoder = new CustomPasswordEncoder();
        Account newAccount = new Account();

        if (PasswordValidatorService.matches(passInput.getValue(), rePassInput.getValue())
                && PasswordValidatorService.validatePassword(passInput.getValue())
                && userService.getByEmail(emailInput.getValue()).size() == 0) {
            newAccount.setEmailAddress(emailInput.getValue());
            newAccount.setPassword(encoder.encode(passInput.getValue()));

            ErrorService errorService = accountService.save(newAccount);
            if (errorService.isErrorStatus()) {
                NotificationService.showNotification(NotificationVariant.LUMO_ERROR,
                        errorService.getErrorMessage());
            } else {
                newAccount = accountService.getByEmail(newAccount.getEmailAddress()).get(0);
                User newUser = new User();
                newUser.setEmail(emailInput.getValue());
                newUser.setAccount(newAccount);
                newUser.setEnabled(true);
                newUser.setActivated(false);

                // TODO: Handle Error
                ErrorService userErrorService = userService.saveUser(newUser);
                if (userErrorService.isErrorStatus()) {
                    NotificationService.showNotification(NotificationVariant.LUMO_ERROR,
                            userErrorService.getErrorMessage());
                } else {
                    RoleMap roleMap = new RoleMap();
                    roleMap.setUser(newUser);

                    roleMapService.add(roleMap);

                    NotificationService.showNotification(NotificationVariant.LUMO_SUCCESS, "Success");
                    UI.getCurrent().getPage().setLocation("/login");
                }
            }
        } else if (userService.getByEmail(emailInput.getValue()).size() > 0) {
            NotificationService.showNotification(NotificationVariant.LUMO_ERROR,
                    "Your email already invited to one of company registered in our system. If you feel that you haven't invited yet, please check your email for new invitation link we already sent",
                    8);
            try {
                MailSenderService.sendEmail(mailSender, emailInput.getValue(), "Invitation",
                        "http://localhost:8080/register?email=".concat(emailInput.getValue()));
            } catch (UnsupportedEncodingException | MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            NotificationService.showNotification(NotificationVariant.LUMO_ERROR, "Password Doesn't Match!");
        }
    }

    private void addInvitedUser(User user) {
        CustomPasswordEncoder encoder = new CustomPasswordEncoder();
        Account newAccount = new Account();

        if (PasswordValidatorService.matches(passInput.getValue(), rePassInput.getValue())
                && PasswordValidatorService.validatePassword(passInput.getValue())) {
            newAccount.setEmailAddress(emailInput.getValue());
            newAccount.setPassword(encoder.encode(passInput.getValue()));

            ErrorService errorService = accountService.save(newAccount);
            if (errorService.isErrorStatus()) {
                NotificationService.showNotification(NotificationVariant.LUMO_ERROR,
                        errorService.getErrorMessage());
            } else {
                user.setAccount(newAccount);

                ErrorService userErrorService = userService.editData(user);
                if (userErrorService.isErrorStatus()) {
                    NotificationService.showNotification(NotificationVariant.LUMO_ERROR,
                            userErrorService.getErrorMessage());
                } else {
                    NotificationService.showNotification(NotificationVariant.LUMO_SUCCESS, "Success");
                    UI.getCurrent().getPage().setLocation("/login");
                }
            }
        } else {
            NotificationService.showNotification(NotificationVariant.LUMO_ERROR, "Password Doesn't Match!");
        }
    }

    private void validatePasswordInput() {
        if (PasswordValidatorService.matches(passInput.getValue(), rePassInput.getValue())
                && PasswordValidatorService.validatePassword(passInput.getValue())) {
            btnSaveUser.setEnabled(true);
        } else {
            btnSaveUser.setEnabled(false);

            if (!PasswordValidatorService.matches(passInput.getValue(), rePassInput.getValue())) {
                errorMessage.add(new Span("Password Doesn't Match!"));
            }
            if (!PasswordValidatorService.haveLowercase(passInput.getValue())) {
                errorMessage.add(new Span("Must Contain Lowercase Letter"));
            }
            if (!PasswordValidatorService.haveUppercase(passInput.getValue())) {
                errorMessage.add(new Span("Must Contain Uppercase Letter"));
            }
            if (!PasswordValidatorService.haveNumeric(passInput.getValue())) {
                errorMessage.add(new Span("Must Contain Numeric Character"));
            }
            if (!PasswordValidatorService.haveSymbol(passInput.getValue())) {
                errorMessage.add(new Span("Must Contain Symbol"));
            }
        }
    }
}