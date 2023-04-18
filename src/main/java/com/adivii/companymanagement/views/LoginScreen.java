package com.adivii.companymanagement.views;

import javax.servlet.http.HttpSession;

import com.adivii.companymanagement.data.service.AccountService;
import com.adivii.companymanagement.data.service.RoleMapService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.html.H1;
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

        loginForm = new LoginForm();
        loginForm.setAction("login");

        add(new H1("Login Form"), loginForm);
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