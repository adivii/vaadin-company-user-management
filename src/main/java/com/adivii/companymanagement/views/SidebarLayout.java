package com.adivii.companymanagement.views;

import javax.servlet.http.HttpSession;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.adivii.companymanagement.data.entity.Role;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinServletRequest;

public class SidebarLayout extends VerticalLayout {
    HttpSession session;
    UserService userService;

    public SidebarLayout(UserService userService) {
        this.userService = userService;
        this.session = SessionService.getCurrentSession();

        Button navDashboard = new Button("Dashboard");
        Button navCompanyList = new Button("Company List");
        Button navDepartmentList = new Button("Department List");
        Button navUserList = new Button("User List");
        Button navUserSetting = new Button("User Setting");
        Button btnLogout = new Button("Logout");

        if (this.session.getAttribute("userID") != null) {
            Role role = this.userService.getUser((Integer) session.getAttribute("userID")).get().getRoleId().getRole();

            if (role.getValue().equals("superadmin")) {
                navCompanyList.setVisible(true);
                navDepartmentList.setVisible(true);
                navUserList.setVisible(true);
            } else if (role.getValue().equals("companyadmin")) {
                navCompanyList.setVisible(true);
                navDepartmentList.setVisible(true);
                navUserList.setVisible(true);
            } else if (role.getValue().equals("departmentadmin")) {
                navCompanyList.setVisible(false);
                navDepartmentList.setVisible(true);
                navUserList.setVisible(true);
            } else if (role.getValue().equals("useradmin")) {
                navCompanyList.setVisible(false);
                navDepartmentList.setVisible(false);
                navUserList.setVisible(true);
            } else {
                navCompanyList.setVisible(false);
                navDepartmentList.setVisible(false);
                navUserList.setVisible(false);
            }

            navDashboard.setVisible(true);
            navUserSetting.setVisible(true);
            btnLogout.setVisible(true);
        }

        navDashboard.setWidthFull();
        navCompanyList.setWidthFull();
        navDepartmentList.setWidthFull();
        navUserList.setWidthFull();
        navUserSetting.setWidthFull();
        btnLogout.setWidthFull();

        // Navigasi
        navDashboard.addClickListener(e -> {
            UI.getCurrent().navigate(DashboardScreen.class);
        });
        navCompanyList.addClickListener(e -> {
            UI.getCurrent().navigate(CompanyList.class);
        });
        navDepartmentList.addClickListener(e -> {
            UI.getCurrent().navigate(DepartmentList.class);
        });
        navUserList.addClickListener(e -> {
            UI.getCurrent().navigate(UserList.class);
        });
        navUserSetting.addClickListener(e -> {
            UI.getCurrent().getPage().setLocation("/setting");
        });
        btnLogout.addClickListener(e -> {
            UI.getCurrent().getPage().setLocation("/");
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(
                    VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                    null);
        });

        add(navDashboard, navCompanyList, navDepartmentList, navUserList, navUserSetting, btnLogout);
    }
}
