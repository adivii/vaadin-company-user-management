package com.adivii.companymanagement.views;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.DepartmentService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("/department")
@PageTitle("Department List")
public class DepartmentList extends HorizontalLayout {
    private DepartmentService departmentService;
    private CompanyService companyService;
    private Grid<Department> departmentTable;

    public DepartmentList(DepartmentService departmentService, CompanyService companyService) {
        this.departmentService = departmentService;
        this.companyService = companyService;

        VerticalLayout sidebarLayout = new SidebarLayout();

        sidebarLayout.setWidth("250px");

        setSizeFull();
        add(sidebarLayout, getLayout());
    }
    
    public Grid<Department> getDepartmentTable() {
        this.departmentTable = new Grid<>(Department.class, false);

        this.departmentTable.addComponentColumn(e -> {
            Button button = getDeleteButton(e);

            return button;
        }).setWidth("75px").setFlexGrow(0);
        this.departmentTable.addColumn(Department::getName).setHeader("Department").setAutoWidth(true).setResizable(true);
        this.departmentTable.addColumn(e -> e.getCompanyId().getCompanyName()).setHeader("Company").setAutoWidth(true).setResizable(true);
        this.departmentTable.addColumn(Department::getUserCount).setHeader("No of Employee").setAutoWidth(true).setResizable(true);

        this.departmentTable.addItemClickListener(e -> {
            getEditDialog(e.getItem()).open();
        });

        this.departmentTable.setHeightFull();

        updateTable();

        return this.departmentTable;
    }

    public void updateTable() {
        this.departmentTable.setItems(departmentService.getAllDepartment());
    }

    public VerticalLayout getLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        Button btnAdd = new Button("Add New Department");
        
        btnAdd.addClickListener(e -> {
            getAddDepartmentDialog().open();
        });

        mainLayout.add(btnAdd, getDepartmentTable());
        mainLayout.setHeightFull();

        return mainLayout;
    }

    public Dialog getAddDepartmentDialog() {
        Dialog addDepartmentDialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        H3 title = new H3("Add New Department");

        // Input Field
        TextField nameInput = new TextField("Department Name");
        ComboBox<Company> companyInput = new ComboBox<>("Company Name");
        companyInput.setItems(companyService.getAllCompany());
        companyInput.setItemLabelGenerator(Company::getCompanyName);

        // Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> addDepartmentDialog.close());
        buttonLayout.add(btnSave, btnCancel);

        // Save Mechanism
        btnSave.addClickListener(e -> {
            Department newDepartment = new Department();
            newDepartment.setName(nameInput.getValue());
            newDepartment.setCompanyId(companyInput.getValue());

            if(departmentService.saveDepartment(newDepartment)) {
                addDepartmentDialog.close();
                updateTable();
            } else {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text("Can't Save Department");
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();
            }  
        });

        dialogLayout.add(title, nameInput, companyInput, buttonLayout);
        addDepartmentDialog.add(dialogLayout);

        // Disable Close Without Button
        addDepartmentDialog.setModal(true);
        addDepartmentDialog.setCloseOnEsc(false);
        addDepartmentDialog.setCloseOnOutsideClick(false);

        return addDepartmentDialog;
    }

    public Dialog getEditDialog(Department department) {
        Dialog editDepartmentDialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        H3 title = new H3("Edit Department");

        // Input Field
        TextField nameInput = new TextField("Department Name");
        ComboBox<Company> companyInput = new ComboBox<>("Company Name");
        companyInput.setItems(companyService.getAllCompany());
        companyInput.setItemLabelGenerator(Company::getCompanyName);

        // Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> editDepartmentDialog.close());
        buttonLayout.add(btnSave, btnCancel);

        // Set Value
        nameInput.setValue(department.getName());
        companyInput.setValue(department.getCompanyId());

        // Save Mechanism
        btnSave.addClickListener(e -> {
            Department newDepartment = department;
            newDepartment.setName(nameInput.getValue());
            newDepartment.setCompanyId(companyInput.getValue());

            if(departmentService.editData(newDepartment)) {
                editDepartmentDialog.close();
                updateTable();
            } else {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text("Can't Save Department");
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();
            } 
        });

        dialogLayout.add(title, nameInput, companyInput, buttonLayout);
        editDepartmentDialog.add(dialogLayout);

        // Disable Close Without Button
        editDepartmentDialog.setModal(true);
        editDepartmentDialog.setCloseOnEsc(false);
        editDepartmentDialog.setCloseOnOutsideClick(false);

        return editDepartmentDialog;
    }

    public Button getDeleteButton(Department department) {
        Button btnDelete = new Button(new Icon(VaadinIcon.TRASH));
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);

        // Create Confirmation Dialog
        Dialog confirmationDialog = new Dialog();
        VerticalLayout confirmationLayout = new VerticalLayout();

        Text confirmationText = new Text("Are you sure?");
        Button confirmationButton = new Button("Yes", e -> {
            if(departmentService.deleteDepartment(department)) {
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
