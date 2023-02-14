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
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("/department")
@PageTitle("Department List")
public class DepartmentList extends HorizontalLayout {
    private DepartmentService departmentService;
    private CompanyService companyService;

    public DepartmentList(DepartmentService departmentService, CompanyService companyService) {
        this.departmentService = departmentService;
        this.companyService = companyService;

        VerticalLayout sidebarLayout = new SidebarLayout();

        sidebarLayout.setWidth("250px");

        setSizeFull();
        add(sidebarLayout, getLayout());
    }
    
    public Grid<Department> getDepartmentTable() {
        Grid<Department> departmentTable = new Grid<>(Department.class, false);

        departmentTable.addColumn(Department::getName).setHeader("Department Name");
        departmentTable.addColumn(e -> e.getCompanyId().getCompanyName()).setHeader("Company Name");
        departmentTable.addColumn(Department::getUserCount).setHeader("No of Employee");
        departmentTable.addComponentColumn(e -> {
            Button button = getDeleteButton(e);

            return button;
        }).setHeader("Operation");

        departmentTable.addItemClickListener(e -> {
            getEditDialog(e.getItem()).open();
        });

        departmentTable.setHeightFull();

        departmentTable.setItems(departmentService.getAllDepartment());

        return departmentTable;
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

            departmentService.saveDepartment(newDepartment);
            addDepartmentDialog.close();
            UI.getCurrent().getPage().reload();
        });

        dialogLayout.add(nameInput, companyInput, buttonLayout);
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

            departmentService.saveDepartment(newDepartment);
            editDepartmentDialog.close();
            UI.getCurrent().getPage().reload();
        });

        dialogLayout.add(nameInput, companyInput, buttonLayout);
        editDepartmentDialog.add(dialogLayout);

        // Disable Close Without Button
        editDepartmentDialog.setModal(true);
        editDepartmentDialog.setCloseOnEsc(false);
        editDepartmentDialog.setCloseOnOutsideClick(false);

        return editDepartmentDialog;
    }

    public Button getDeleteButton(Department department) {
        Button btnDelete = new Button("Delete");
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        // Create Confirmation Dialog
        Dialog confirmationDialog = new Dialog();
        VerticalLayout confirmationLayout = new VerticalLayout();

        Text confirmationText = new Text("Are you sure?");
        Button confirmationButton = new Button("Yes", e -> {
            if(departmentService.deleteDepartment(department)) {
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
