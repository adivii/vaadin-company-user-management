package com.adivii.companymanagement.views;

import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route("/")
@RouteAlias("/dashboard")
@PageTitle("Dashboard")
public class DashboardScreen extends HorizontalLayout {
    HttpSession session;
    UserService userService;

    public DashboardScreen(UserService userService) {
        this.userService = userService;
        this.session = SessionService.getCurrentSession();

        if(this.session.getAttribute("userID") != null) {
            User user = (User) this.session.getAttribute("userID");
            NewPasswordDialog newPasswordDialog = new NewPasswordDialog(this.userService);

            if(!user.isActivated()) {
                newPasswordDialog.open();
            }
        }

        VerticalLayout sidebar = new SidebarLayout();
        VerticalLayout mainLayout = new VerticalLayout(new H1("Welcome, " + ((User) session.getAttribute("userID")).getFirstName() + "!"));

        sidebar.setWidth("250px");

        setSizeFull();
        add(sidebar, mainLayout);
    }
}
