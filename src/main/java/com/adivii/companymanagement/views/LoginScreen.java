package com.adivii.companymanagement.views;

import java.io.File;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpSession;

import com.adivii.companymanagement.data.entity.Account;
import com.adivii.companymanagement.data.service.RoleService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.file_upload.ProfilePictureUpload;
import com.adivii.companymanagement.data.service.generator.RoleDataGenerator;
import com.adivii.companymanagement.data.service.security.CustomBase64Encoder;
import com.adivii.companymanagement.data.service.security.CustomPasswordEncoder;
import com.adivii.companymanagement.data.service.security.GuitarChordEncoder;
import com.adivii.companymanagement.data.service.security.UserAuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

// TODO: Search for better authorization logic
// TODO: Rebuild Login Screen (build custom)

@Route("/login")
@PageTitle("Login Screen")
public class LoginScreen extends VerticalLayout {
    // Service
    private UserAuthService userAuthService;

    // Field
    private EmailField inputUser;
    private PasswordField inputPass;

    // Button
    private Button btnLogin;

    public LoginScreen(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;

        setupField();
        getLayout();

        setupOnClickListener();
    }

    private void setupField() {
        this.inputUser = new EmailField("Email Address");
        this.inputPass = new PasswordField("Password");
        this.btnLogin = new Button("Login");
    }

    private void getLayout() {
        add(this.inputUser, this.inputPass, btnLogin);
    }

    private void setupOnClickListener() {
        this.btnLogin.addClickListener(clickEvent -> {
            Account account = userAuthService.authenticateLogin(this.inputUser.getValue(), this.inputPass.getValue()); 
            // add(account.getEmailAddress());
            System.out.println("Done");
        });
    }
}
