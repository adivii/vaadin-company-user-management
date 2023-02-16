package com.adivii.companymanagement.views;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

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

    Grid<Company> companyTable;

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
        this.companyTable = new Grid<>(Company.class, false);

        this.companyTable.addComponentColumn(e -> {
            Button button = getDeleteButton(e);

            return button;
        }).setWidth("75px").setFlexGrow(0).setFrozen(true);
        this.companyTable.addColumn(Company::getCompanyName).setHeader("Company Name").setAutoWidth(true).setResizable(true);
        this.companyTable.addColumn(Company::getAddress).setHeader("Address").setAutoWidth(true).setResizable(true);
        this.companyTable.addColumn(Company::getSector).setHeader("Sector").setAutoWidth(true).setResizable(true);
        this.companyTable.addColumn(Company::getUserCount).setHeader("No of Employee").setAutoWidth(true).setResizable(true);
        this.companyTable.addComponentColumn(e -> {
            Anchor link = new Anchor(e.getWebsite(), e.getWebsite());

            return link;
        }).setHeader("Website").setAutoWidth(true).setResizable(true);

        this.companyTable.addItemClickListener(e -> {
            getEditCompanyDialog(e.getItem()).open();
        });

        updateTable();
        this.companyTable.setHeightFull();

        return this.companyTable;
    }

    public void updateTable() {
        this.companyTable.setItems(companyService.getAllCompany());
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
        H3 title = new H3("Add New Company");
        Hr divider = new Hr();

        divider.setWidthFull();
        divider.setHeight("3px");

        // Create Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> addCompanyDialog.close());
        
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(btnSave, btnCancel);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();

        // Text Field
        TextField nameInput = new TextField("Company Name");
        TextArea addressInput = new TextArea("Company Address");
        TextField sectorInput = new TextField("Company Sector");
        TextField websiteInput = new TextField("Company Website");

        nameInput.setWidthFull();
        sectorInput.setWidthFull();
        websiteInput.setWidthFull();
        addressInput.setWidthFull();
        addressInput.setMaxLength(255);
        addressInput.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/255");
        });
        addressInput.setHelperText("0/255");
        addressInput.setValueChangeMode(ValueChangeMode.EAGER);

        Scroller scroller = new Scroller(new Div(nameInput, addressInput, sectorInput, websiteInput));
        scroller.setHeightFull();
        scroller.setWidthFull();
        scroller.getStyle()
            .set("padding", "var(--lumo-space-s)");

        // Save Data
        btnSave.addClickListener(e -> {
            Company newCompany = new Company();
            newCompany.setCompanyName(nameInput.getValue());
            newCompany.setAddress(addressInput.getValue());
            newCompany.setSector(sectorInput.getValue());
            newCompany.setWebsite(websiteInput.getValue());
            
            if(companyService.addCompany(newCompany)) {
                addCompanyDialog.close();
                updateTable();
            } else {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text("Can't Save Company");
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();
            }
        });

        dialogLayout.setWidth("500px");
        dialogLayout.setHeight("500px");
        dialogLayout.add(title, divider, scroller, buttonLayout);
        addCompanyDialog.add(dialogLayout);
        
        return addCompanyDialog;
    }

    public Dialog getEditCompanyDialog(Company data) {
        Dialog editCompanyDialog = new Dialog();
        editCompanyDialog.setModal(true);
        editCompanyDialog.setCloseOnEsc(false);
        editCompanyDialog.setCloseOnOutsideClick(false);
        

        VerticalLayout dialogLayout = new VerticalLayout();
        H3 title = new H3("Edit Company");
        Hr divider = new Hr();

        divider.setWidthFull();
        divider.setHeight("3px");

        // Create Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> editCompanyDialog.close());
       
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(btnSave, btnCancel);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();

        // Text Field
        TextField nameInput = new TextField("Company Name");
        TextArea addressInput = new TextArea("Company Address");
        TextField sectorInput = new TextField("Company Sector");
        TextField websiteInput = new TextField("Company Website");

        nameInput.setWidthFull();
        sectorInput.setWidthFull();
        websiteInput.setWidthFull();
        addressInput.setWidthFull();
        addressInput.setMaxLength(255);
        addressInput.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/255");
        });
        addressInput.setHelperText("0/255");
        addressInput.setValueChangeMode(ValueChangeMode.EAGER);

        Scroller scroller = new Scroller(new Div(nameInput, addressInput, sectorInput, websiteInput));
        scroller.setHeightFull();
        scroller.setWidthFull();
        scroller.getStyle()
            .set("padding", "var(--lumo-space-s)");

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

            if(companyService.editData(currentCompany)) {
                editCompanyDialog.close();
                updateTable();
            } else {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text("Can't Save Company");
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();
            }
        });

        dialogLayout.setWidth("500px");
        dialogLayout.setHeight("500px");
        dialogLayout.add(title, divider, scroller, buttonLayout);
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
                updateTable();
            } else {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text("Can't Delete Record");
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();

                confirmationDialog.close();
            }
        });
        Button cancelButton = new Button("No", e -> confirmationDialog.close());
        HorizontalLayout buttonLayout = new HorizontalLayout(confirmationButton, cancelButton);

        confirmationLayout.add(confirmationText, buttonLayout);
        confirmationLayout.setAlignItems(Alignment.CENTER);
        confirmationDialog.add(confirmationLayout);

        btnDelete.addClickListener(e -> {
            confirmationDialog.open();
        });

        return btnDelete;
    }
}
