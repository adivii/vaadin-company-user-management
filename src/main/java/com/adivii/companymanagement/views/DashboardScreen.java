package com.adivii.companymanagement.views;

import javax.servlet.http.HttpSession;

import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route("/")
@RouteAlias("/dashboard")
@PageTitle("Dashboard")
public class DashboardScreen extends HorizontalLayout implements BeforeEnterObserver {
    HttpSession session;
    UserService userService;

    public DashboardScreen(UserService userService) {
        this.userService = userService;
        this.session = SessionService.getCurrentSession();

        VerticalLayout sidebar = new SidebarLayout(this.userService);
        VerticalLayout mainLayout = new VerticalLayout(new H1("Welcome, " + userService.getUser((Integer) this.session.getAttribute("userID")).get().getFirstName() + "!"));

        sidebar.setWidth("250px");

        setSizeFull();
        add(sidebar, mainLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(this.session.getAttribute("userID") != null) {
            User user = userService.getUser((Integer) this.session.getAttribute("userID")).get();

            if(!user.isActivated()) {
                event.forwardTo(UserActivationForm.class);
            }
        } 
    }
}
