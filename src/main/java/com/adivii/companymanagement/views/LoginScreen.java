package com.adivii.companymanagement.views;

import java.io.File;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpSession;

import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.file_upload.ProfilePictureUpload;
import com.adivii.companymanagement.data.service.security.CustomBase64Encoder;
import com.adivii.companymanagement.data.service.security.CustomPasswordEncoder;
import com.adivii.companymanagement.data.service.security.GuitarChordEncoder;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

// TODO: Search for better authorization logic

@Route("/login")
@PageTitle("Login Screen")
public class LoginScreen extends VerticalLayout implements BeforeEnterObserver {
    private final LoginForm loginForm = new LoginForm();
    private UserService userService;
    private HttpSession session;

    public LoginScreen(UserService userService) {
        this.userService = userService;
        session = SessionService.getCurrentSession();

        // Redirect if user already login
        if (session.getAttribute("userID") != null) {
            UI.getCurrent().getPage().setLocation("/");
        }

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        loginForm.setAction("login");
        add(new H1("Login Form"), loginForm);
        // CustomAvatar avatar = new CustomAvatar("Adi Wijaya");
        // avatar.setColor(CustomAvatar.COLOR_GREEN);

        // for(int i = 0;i < CustomBase64Encoder.splitSequence(CustomBase64Encoder.getBinarySequence("Adi")).size();i++){
            // CustomPasswordEncoder customPasswordEncoder = new CustomPasswordEncoder();
            // byte[] salt = customPasswordEncoder.getSaltSt();
            // String stringSalt = customPasswordEncoder.getStringSalt();
            // add(new Div(customPasswordEncoder.encode("sweet14:)")));
            // byte[] salt = customPasswordEncoder.getSalt();
            
            // System.out.println();
        // }

        // ProfilePictureUpload.saveFile(new File("/home/adivii/Documents/aaaa"), "tes");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent arg0) {
        if (arg0.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }

}
