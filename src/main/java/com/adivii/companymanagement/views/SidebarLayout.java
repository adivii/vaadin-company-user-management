package com.adivii.companymanagement.views;

import javax.servlet.http.HttpSession;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinServletRequest;

public class SidebarLayout extends VerticalLayout {
    HttpSession session;

    public SidebarLayout() {
        this.session = SessionService.getCurrentSession();

        Button navDashboard = new Button("Dashboard");
        Button navCompanyList = new Button("Company List");
        Button navDepartmentList = new Button("Department List");
        Button navUserList = new Button("User List");
        Button btnLogout = new Button("Logout");

        if (this.session.getAttribute("userID") != null) {
            String role = ((User) this.session.getAttribute("userID")).getRole();

            if (role.equals("superadmin")) {
                navCompanyList.setVisible(true);
                navDepartmentList.setVisible(true);
                navUserList.setVisible(true);
            } else if (role.equals("companyadmin")) {
                navCompanyList.setVisible(true);
                navDepartmentList.setVisible(false);
                navUserList.setVisible(false);
            } else if (role.equals("departmentadmin")) {
                navCompanyList.setVisible(false);
                navDepartmentList.setVisible(true);
                navUserList.setVisible(false);
            } else if (role.equals("useradmin")) {
                navCompanyList.setVisible(false);
                navDepartmentList.setVisible(false);
                navUserList.setVisible(true);
            }
        }

        navCompanyList.setWidthFull();
        navDepartmentList.setWidthFull();
        navUserList.setWidthFull();
        btnLogout.setWidthFull();

        // Navigasi
        navDashboard.addClickListener(e -> {
            UI.getCurrent().getPage().setLocation("/dashboard");
        });
        navCompanyList.addClickListener(e -> {
            UI.getCurrent().getPage().setLocation("/company");
        });
        navDepartmentList.addClickListener(e -> {
            UI.getCurrent().getPage().setLocation("/department");
        });
        navUserList.addClickListener(e -> {
            UI.getCurrent().getPage().setLocation("/user");
        });
        btnLogout.addClickListener(e -> {
            UI.getCurrent().getPage().setLocation("/");
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(
                    VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                    null);
        });

        add(navCompanyList, navDepartmentList, navUserList, btnLogout);
    }
}
