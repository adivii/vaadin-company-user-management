package com.adivii.companymanagement.views;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpSession;

import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.AccountService;
import com.adivii.companymanagement.data.service.NotificationService;
import com.adivii.companymanagement.data.service.PasswordValidatorService;
import com.adivii.companymanagement.data.service.RoleMapService;
import com.adivii.companymanagement.data.service.RoleService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.security.CustomPasswordEncoder;
import com.adivii.companymanagement.data.service.security.UserAuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

// TODO: Search for better authorization logic

@Route("/login")
@PageTitle("Login Screen")
public class LoginScreen extends VerticalLayout implements BeforeEnterObserver {
    private UserService userService;
    private AccountService accountService;
    private RoleMapService roleMapService;
    private HttpSession session;

    private LoginForm loginForm;

    public LoginScreen(UserService userService, AccountService accountService, RoleMapService roleMapService) {
        this.userService = userService;
        this.accountService = accountService;
        this.roleMapService = roleMapService;
        session = SessionService.getCurrentSession();

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // VerticalLayout mainLayout = new VerticalLayout();
        // EmailField inputEmail = new EmailField("Email Address");
        // PasswordField inputPass = new PasswordField("Password");
        // Button btnLogin = new Button("Login");

        // mainLayout.add(inputEmail, inputPass, btnLogin);

        // // loginForm.setAction("login");
        // btnLogin.addClickListener(loginEvent -> {
        //     UserAuthService userAuthService = new UserAuthService(userService, accountService, roleMapService);

        //     User loggedUser = userAuthService.authenticateLogin(inputEmail.getValue(), inputPass.getValue());
            
        //     if(loggedUser == null) {
        //         NotificationService.showNotification(NotificationVariant.LUMO_ERROR, "Invalid Username and/or Password");
        //     } else {
        //         session.setAttribute("userID", loggedUser.getUserId());
        //         UI.getCurrent().navigate(DashboardScreen.class);
        //     }
        // });

        loginForm = new LoginForm();
        loginForm.setAction("login");

        add(new H1("Login Form"), loginForm);
        // PasswordValidatorService.validatePassword("pass");
        // CustomPasswordEncoder custom = new CustomPasswordEncoder();
        // custom.matches("12345678", "fc93201c7e18235a064c75f14e6a6b229ed9a1d975dd8db816448da1338644cb$[B@d8aff93");
        // CheckboxGroup<String> group = new CheckboxGroup<>();
        // group.setLabel("Opsi");
        // group.setItems(new ArrayList<String>(Arrays.asList("Opsi 1", "Opsi 2", "Opsi 3")));
        // group.addValueChangeListener(event -> {
        //     System.out.println(group.getValue());
        // });
        // add(group);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Redirect if user already login
        if (session.getAttribute("userID") != null) {
            event.forwardTo(DashboardScreen.class);
        }

        if (event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }

}