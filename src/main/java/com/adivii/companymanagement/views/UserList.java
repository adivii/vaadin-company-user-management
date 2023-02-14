package com.adivii.companymanagement.views;

import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.DepartmentService;
import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("/user")
@PageTitle("User List")
public class UserList extends HorizontalLayout {

    UserService userService;
    CompanyService companyService;
    DepartmentService departmentService;

    public UserList(UserService userService, CompanyService companyService, DepartmentService departmentService) {
        this.userService = userService;
        this.companyService = companyService;
        this.departmentService = departmentService;

        VerticalLayout sidebar = new SidebarLayout();

        sidebar.setWidth("250px");

        setSizeFull();
        add(sidebar, getLayout());
    }
    
    public Grid<User> getUserTable() {
        Grid<User> userTable = new Grid<>(User.class, false);

        userTable.addColumn(User::getFirstName).setHeader("First Name");
        userTable.addColumn(User::getLastName).setHeader("Last Name");
        userTable.addColumn(User::getEmailAddress).setHeader("Email");
        userTable.addColumn(User::getAddress).setHeader("Address");
        userTable.addColumn(User::getPhoneNumber).setHeader("Phone");
        userTable.addColumn(e -> e.getDepartmentId().getCompanyId().getCompanyName()).setHeader("Company Name");
        userTable.addColumn(e -> e.getDepartmentId().getName()).setHeader("Department Name");
        userTable.addComponentColumn(e -> {
            Button button = getDeleteButton(e);

            return button;
        }).setHeader("Operation");

        userTable.addItemClickListener(e -> {
            getEditUserDialog(e.getItem()).open();
        });

        userTable.setHeightFull();

        userTable.setItems(this.userService.getAllUser());

        return userTable;
    }

    public VerticalLayout getLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        Button btnAdd = new Button("Add New User");

        btnAdd.addClickListener(e -> {
            getAddUserDialog().open();
        });

        mainLayout.add(btnAdd, getUserTable());
        mainLayout.setHeightFull();

        return mainLayout;
    }

    public Dialog getAddUserDialog() {
        Dialog addDialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();

        // Input Field
        TextField inputFirst = new TextField("First Name");
        TextField inputLast = new TextField("Last Field");
        EmailField inputEmail = new EmailField("Email");
        TextField inputAddress = new TextField("Address");
        TextField inputPhone = new TextField("Phone Number");
        new PhoneI18nFieldFormatter(PhoneI18nFieldFormatter.REGION_ID).extend(inputPhone);
        
        ComboBox<Company> inputCompany = new ComboBox<>("Company Name");
        inputCompany.setItems(companyService.getAllCompany());
        inputCompany.setItemLabelGenerator(Company::getCompanyName);

        ComboBox<Department> inputDepartment = new ComboBox<>("Department Name");
        inputDepartment.setItemLabelGenerator(Department::getName);

        inputCompany.addValueChangeListener(e -> {
            inputDepartment.clear();
            inputDepartment.setItems(departmentService.getByCompany(e.getValue()));
        });

        // Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> addDialog.close());
        buttonLayout.add(btnSave, btnCancel);

        // Save Mechanism
        btnSave.addClickListener(e -> {
            User newUser = new User();
            newUser.setFirstName(inputFirst.getValue());
            newUser.setLastName(inputLast.getValue());;
            newUser.setEmailAddress(inputEmail.getValue());
            newUser.setPhoneNumber(inputPhone.getValue());
            newUser.setAddress(inputAddress.getValue());
            newUser.setDepartmentId(inputDepartment.getValue());

            userService.saveUser(newUser);
            addDialog.close();
            UI.getCurrent().getPage().reload();
        });

        dialogLayout.add(inputFirst, inputLast, inputEmail, inputAddress, inputPhone, inputCompany, inputDepartment, buttonLayout);
        addDialog.add(dialogLayout);

        return addDialog;
    }

    public Dialog getEditUserDialog(User user) {
        Dialog editDialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();

        // Input Field
        TextField inputFirst = new TextField("First Name");
        TextField inputLast = new TextField("Last Field");
        EmailField inputEmail = new EmailField("Email");
        TextField inputAddress = new TextField("Address");
        TextField inputPhone = new TextField("Phone Number");
        new PhoneI18nFieldFormatter(PhoneI18nFieldFormatter.REGION_ID).extend(inputPhone);
        
        ComboBox<Company> inputCompany = new ComboBox<>("Company Name");
        inputCompany.setItems(companyService.getAllCompany());
        inputCompany.setItemLabelGenerator(Company::getCompanyName);

        ComboBox<Department> inputDepartment = new ComboBox<>("Department Name");
        inputDepartment.setItemLabelGenerator(Department::getName);

        inputCompany.addValueChangeListener(e -> {
            inputDepartment.clear();
            inputDepartment.setItems(departmentService.getByCompany(e.getValue()));
        });

        // Set Value
        inputFirst.setValue(user.getFirstName());
        inputLast.setValue(user.getLastName());
        inputEmail.setValue(user.getEmailAddress());
        inputAddress.setValue(user.getAddress());
        inputPhone.setValue(user.getPhoneNumber());
        inputCompany.setValue(user.getDepartmentId().getCompanyId());
        inputDepartment.setValue(user.getDepartmentId());

        // Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> editDialog.close());
        buttonLayout.add(btnSave, btnCancel);

        // Save Mechanism
        btnSave.addClickListener(e -> {
            User newUser = user;
            newUser.setFirstName(inputFirst.getValue());
            newUser.setLastName(inputLast.getValue());;
            newUser.setEmailAddress(inputEmail.getValue());
            newUser.setPhoneNumber(inputPhone.getValue());
            newUser.setAddress(inputAddress.getValue());
            newUser.setDepartmentId(inputDepartment.getValue());

            userService.editData(newUser);
            editDialog.close();
            UI.getCurrent().getPage().reload();
        });

        dialogLayout.add(inputFirst, inputLast, inputEmail, inputAddress, inputPhone, inputCompany, inputDepartment, buttonLayout);
        editDialog.add(dialogLayout);

        return editDialog;
    }

    public Button getDeleteButton(User user) {
        Button btnDelete = new Button("Delete");
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        // Create Confirmation Dialog
        Dialog confirmationDialog = new Dialog();
        VerticalLayout confirmationLayout = new VerticalLayout();

        Text confirmationText = new Text("Are you sure?");
        Button confirmationButton = new Button("Yes", e -> {
            userService.deleteUser(user);;
            confirmationDialog.close();
            UI.getCurrent().getPage().reload();
        });
        confirmationLayout.add(confirmationText, confirmationButton);
        confirmationDialog.add(confirmationLayout);

        btnDelete.addClickListener(e -> {
            confirmationDialog.open();
        });

        return btnDelete;
    }
}
