package com.adivii.companymanagement.views;

import javax.servlet.http.HttpSession;

import com.adivii.companymanagement.data.service.RoleService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
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

    public LoginScreen(UserService userService, RoleService roleService) {
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