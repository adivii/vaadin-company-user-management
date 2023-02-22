package com.adivii.companymanagement.views;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("/login")
@PageTitle("Login Screen")
public class LoginScreen extends VerticalLayout implements BeforeEnterObserver {
    private final LoginForm loginForm = new LoginForm();
    private UserService userService;
    private VaadinSession session;

    public LoginScreen(UserService userService) {
        this.userService = userService;
        session = VaadinSession.getCurrent();

        // Redirect if user already login
        if(session.getAttribute("userID") != null) {
            UI.getCurrent().getPage().setLocation("/company");
        }

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        loginForm.addLoginListener(e -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        });
        
        loginForm.setAction("login");
        add(new H1("Login Form"), loginForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent arg0) {
        // TODO Auto-generated method stub
        if(arg0.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
                    loginForm.setError(true);
                }
    }

    
}
