package com.adivii.companymanagement.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class SidebarLayout extends VerticalLayout {

    public SidebarLayout() {
        Button navCompanyList = new Button("Company List");
        Button navDepartmentList = new Button("Department List");
        Button navUserList = new Button("User List");

        navCompanyList.setWidthFull();
        navDepartmentList.setWidthFull();
        navUserList.setWidthFull();

        // Navigasi
        navCompanyList.addClickListener(e -> {
            navCompanyList.getUI().ifPresent(ui -> ui.navigate(CompanyList.class));
        });
        navDepartmentList.addClickListener(e -> {
            navDepartmentList.getUI().ifPresent(ui -> ui.navigate(DepartmentList.class));
        });
        navUserList.addClickListener(e -> {
            navUserList.getUI().ifPresent(ui -> ui.navigate(UserList.class));
        });

        add(navCompanyList, navDepartmentList, navUserList);
    }
}
