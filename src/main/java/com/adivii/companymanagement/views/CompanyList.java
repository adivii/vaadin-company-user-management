package com.adivii.companymanagement.views;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.PWA;

// Add alternative Route for this page
// So, both localhost:8080 and localhost:8080/company will open this page
// @PWA(name = "Company Management App",
//      shortName = "cm-app")
@Route("")
@RouteAlias("/company")
@PageTitle("Company List")
public class CompanyList extends HorizontalLayout {
    CompanyService companyService;
    UserService userService;

    public CompanyList(CompanyService companyService, UserService userService) {
        this.companyService = companyService;
        this.userService = userService;

        VerticalLayout sidebar = new SidebarLayout();
        VerticalLayout mainLayout = getLayout();

        sidebar.setWidth("250px");

        setSizeFull();
        add(sidebar, mainLayout);
    }

    public Grid<Company> getCompanyTable() {
        Grid<Company> companyTable = new Grid<>(Company.class, false);

        companyTable.addComponentColumn(e -> {
            Button button = getDeleteButton(e);

            return button;
        }).setWidth("75px").setFlexGrow(0).setFrozen(true);
        companyTable.addColumn(Company::getCompanyName).setHeader("Company Name").setAutoWidth(true).setResizable(true);
        companyTable.addColumn(Company::getAddress).setHeader("Address").setAutoWidth(true).setResizable(true);
        companyTable.addColumn(Company::getSector).setHeader("Sector").setAutoWidth(true).setResizable(true);
        companyTable.addColumn(Company::getUserCount).setHeader("No of Employee").setAutoWidth(true).setResizable(true);
        companyTable.addComponentColumn(e -> {
            Anchor link = new Anchor(e.getWebsite(), e.getWebsite());

            return link;
        }).setHeader("Website").setAutoWidth(true).setResizable(true);

        companyTable.addItemClickListener(e -> {
            getEditCompanyDialog(e.getItem()).open();
        });

        companyTable.setHeightFull();

        companyTable.setItems(companyService.getAllCompany());

        return companyTable;
    }

    public VerticalLayout getLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        Button btnAdd = new Button("Add New Company", e -> getAddCompanyDialog().open());
        Grid<Company> companyTable = getCompanyTable();

        mainLayout.add(btnAdd, companyTable);
        mainLayout.setHeightFull();

        return mainLayout;
    }

    public Dialog getAddCompanyDialog() {
        Dialog addCompanyDialog = new Dialog();
        addCompanyDialog.setModal(true);
        addCompanyDialog.setCloseOnEsc(false);
        addCompanyDialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();

        // Create Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> addCompanyDialog.close());
        buttonLayout.add(btnSave, btnCancel);

        // Text Field
        TextField nameInput = new TextField("Company Name");
        TextField addressInput = new TextField("Company Address");
        TextField sectorInput = new TextField("Company Sector");
        TextField websiteInput = new TextField("Company Website");

        // Save Data
        btnSave.addClickListener(e -> {
            Company newCompany = new Company();
            newCompany.setCompanyName(nameInput.getValue());
            newCompany.setAddress(addressInput.getValue());
            newCompany.setSector(sectorInput.getValue());
            newCompany.setWebsite(websiteInput.getValue());

            companyService.addCompany(newCompany);
            addCompanyDialog.close();
            UI.getCurrent().getPage().reload();
        });

        dialogLayout.add(nameInput, addressInput, sectorInput, websiteInput, buttonLayout);
        addCompanyDialog.add(dialogLayout);
        
        return addCompanyDialog;
    }

    public Dialog getEditCompanyDialog(Company data) {
        Dialog editCompanyDialog = new Dialog();
        editCompanyDialog.setModal(true);
        editCompanyDialog.setCloseOnEsc(false);
        editCompanyDialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();

        // Create Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> editCompanyDialog.close());
        buttonLayout.add(btnSave, btnCancel);

        // Text Field
        TextField nameInput = new TextField("Company Name");
        TextField addressInput = new TextField("Company Address");
        TextField sectorInput = new TextField("Company Sector");
        TextField websiteInput = new TextField("Company Website");

        // Set Content
        nameInput.setValue(data.getCompanyName());
        addressInput.setValue(data.getAddress());
        sectorInput.setValue(data.getSector());
        websiteInput.setValue(data.getWebsite());

        // Save Data
        btnSave.addClickListener(e -> {
            Company currentCompany = data;
            currentCompany.setCompanyName(nameInput.getValue());
            currentCompany.setAddress(addressInput.getValue());
            currentCompany.setSector(sectorInput.getValue());
            currentCompany.setWebsite(websiteInput.getValue());

            companyService.editData(currentCompany);
            editCompanyDialog.close();
            UI.getCurrent().getPage().reload();
        });

        dialogLayout.add(nameInput, addressInput, sectorInput, websiteInput, buttonLayout);
        editCompanyDialog.add(dialogLayout);
        
        return editCompanyDialog;
    }

    public Button getDeleteButton(Company company) {
        Button btnDelete = new Button(new Icon(VaadinIcon.TRASH));
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);

        // Create Confirmation Dialog
        Dialog confirmationDialog = new Dialog();
        VerticalLayout confirmationLayout = new VerticalLayout();

        Text confirmationText = new Text("Are you sure?");
        Button confirmationButton = new Button("Yes", e -> {
            if(companyService.deleteCompany(company)) {
                confirmationDialog.close();
                UI.getCurrent().getPage().reload();
            } else {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text("Can't Delete Record");
                Button closeButton = new Button(new Icon("lumo", "cross"), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();

                confirmationDialog.close();
            }
        });
        confirmationLayout.add(confirmationText, confirmationButton);
        confirmationDialog.add(confirmationLayout);

        btnDelete.addClickListener(e -> {
            confirmationDialog.open();
        });

        return btnDelete;
    }
}
