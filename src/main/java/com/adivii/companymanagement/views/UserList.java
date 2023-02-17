package com.adivii.companymanagement.views;

import java.util.List;
import java.util.function.Consumer;

import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.DepartmentService;
import com.adivii.companymanagement.data.service.UserFilterService;
import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("/user")
@PageTitle("User List")
public class UserList extends HorizontalLayout {

    UserService userService;
    CompanyService companyService;
    DepartmentService departmentService;
    UserFilterService userFilterService;

    Grid<User> userTable;
    final ListDataProvider<User> dataProvider;

    public UserList(UserService userService, CompanyService companyService, DepartmentService departmentService) {
        this.userService = userService;
        this.companyService = companyService;
        this.departmentService = departmentService;
        this.dataProvider = new ListDataProvider<>(this.userService.getAllUser());

        VerticalLayout sidebar = new SidebarLayout();

        sidebar.setWidth("250px");

        setSizeFull();
        add(sidebar, getLayout());
    }

    public Grid<User> getUserTable() {
        this.userTable = new Grid<>(User.class, false);

        this.userTable.addComponentColumn(e -> {
            Button button = getDeleteButton(e);

            return button;
        }).setWidth("75px").setFlexGrow(0).setFrozen(true);
        // TODO : Apply Filter Header
        Grid.Column<User> emailColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                "<span title='[[item.email]]' aria-label='[[item.email]]'>[[item.email]]</span>")
                .withProperty("email", User::getEmailAddress)).setAutoWidth(true).setResizable(true).setFrozen(true);
        Grid.Column<User> firstColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                "<span title='[[item.first]]' aria-label='[[item.first]]'>[[item.first]]</span>")
                .withProperty("first", User::getFirstName)).setAutoWidth(true)
                .setResizable(true);
        Grid.Column<User> lastColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                "<span title='[[item.last]]' aria-label='[[item.last]]'>[[item.last]]</span>")
                .withProperty("last", User::getLastName)).setAutoWidth(true).setResizable(true);
        Grid.Column<User> addressColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                "<span title='[[item.address]]' aria-label='[[item.address]]'>[[item.address]]</span>")
                .withProperty("address", User::getAddress)).setAutoWidth(true).setResizable(true);
        Grid.Column<User> phoneColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                "<span title='[[item.phone]]' aria-label='[[item.phone]]'>[[item.phone]]</span>")
                .withProperty("phone", User::getPhoneNumber)).setAutoWidth(true).setResizable(true);
        Grid.Column<User> companyColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                "<span title='[[item.company]]' aria-label='[[item.company]]'>[[item.company]]</span>")
                .withProperty("company", e -> e.getDepartmentId().getCompanyId().getCompanyName()))
                .setAutoWidth(true).setResizable(true);
        Grid.Column<User> departmentColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                "<span title='[[item.department]]' aria-label='[[item.department]]'>[[item.department]]</span>")
                .withProperty("department", e -> e.getDepartmentId().getName()))
                .setAutoWidth(true).setResizable(true);

        this.userTable.addItemClickListener(e -> {
            getEditUserDialog(e.getItem()).open();
        });

        this.userTable.setHeightFull();

        this.userTable.setDataProvider(this.dataProvider);

        this.userFilterService = new UserFilterService(this.dataProvider);

        this.userTable.getHeaderRows().clear();
        HeaderRow headerRow = this.userTable.appendHeaderRow();

        headerRow.getCell(emailColumn).setComponent(
                createFilterHeader("Email", this.userFilterService::setEmail));
        headerRow.getCell(firstColumn).setComponent(
                createFilterHeader("First Name", this.userFilterService::setFirstName));
        headerRow.getCell(lastColumn).setComponent(
                createFilterHeader("Last Name", this.userFilterService::setLastName));
        headerRow.getCell(addressColumn).setComponent(
                createFilterHeader("Address", this.userFilterService::setAddress));
        headerRow.getCell(phoneColumn).setComponent(
                createFilterHeader("Phone", this.userFilterService::setPhone));
        headerRow.getCell(companyColumn).setComponent(
                createFilterHeader("Company", this.userFilterService::setCompany));
        headerRow.getCell(departmentColumn).setComponent(
                createFilterHeader("Department", this.userFilterService::setDepartment));

        return this.userTable;
    }

    private static Component createFilterHeader(String labelText,
            Consumer<String> filterChangeConsumer) {
        Label label = new Label(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));
        VerticalLayout layout = new VerticalLayout(label, textField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }

    public void updateTable() {
        // TODO: Can't update table when add new record (if using only refreshAll)
        List<User> items = this.userService.getAllUser();
        ListDataProvider<User> provider = new ListDataProvider<>(items);

        // userTable.setItems(items);
        userTable.setDataProvider(provider);
        userFilterService.setDataProvider(provider);
    }

    public VerticalLayout getLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        Button btnAdd = new Button("Add New User");

        getUserTable();

        btnAdd.addClickListener(e -> {
            getAddUserDialog().open();
        });

        mainLayout.add(btnAdd, this.userTable);
        mainLayout.setHeightFull();

        return mainLayout;
    }

    public Dialog getAddUserDialog() {
        Dialog addDialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        H3 title = new H3("Add New User");
        Hr divider = new Hr();

        divider.setWidthFull();
        divider.setHeight("3px");

        // Input Field
        TextField inputFirst = new TextField("First Name");
        TextField inputLast = new TextField("Last Field");
        HorizontalLayout inputName = new HorizontalLayout(inputFirst, inputLast);
        EmailField inputEmail = new EmailField("Email");
        TextArea inputAddress = new TextArea("Address");
        TextField inputPhone = new TextField("Phone Number");
        new PhoneI18nFieldFormatter(PhoneI18nFieldFormatter.REGION_ID).extend(inputPhone);

        inputName.setWidthFull();
        inputFirst.setWidth("50%");
        inputLast.setWidth("50%");
        inputEmail.setWidthFull();
        inputAddress.setWidthFull();
        inputAddress.setMaxLength(255);
        inputAddress.setHelperText("0/255");
        inputAddress.setValueChangeMode(ValueChangeMode.EAGER);
        inputAddress.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/255");
        });
        inputPhone.setWidthFull();

        ComboBox<Company> inputCompany = new ComboBox<>("Company Name");
        inputCompany.setItems(companyService.getAllCompany());
        inputCompany.setItemLabelGenerator(Company::getCompanyName);
        inputCompany.setWidth("50%");

        ComboBox<Department> inputDepartment = new ComboBox<>("Department Name");
        inputDepartment.setItemLabelGenerator(Department::getName);
        inputDepartment.setWidth("50%");

        HorizontalLayout inputCompDept = new HorizontalLayout(inputCompany, inputDepartment);
        inputCompDept.setWidthFull();

        inputCompany.addValueChangeListener(e -> {
            inputDepartment.clear();
            inputDepartment.setItems(departmentService.getByCompany(e.getValue()));
        });

        Scroller scroller = new Scroller(new Div(inputName, inputEmail, inputAddress, inputPhone, inputCompDept));
        scroller.setHeightFull();
        scroller.setWidthFull();
        scroller.getStyle()
                .set("padding", "var(--lumo-space-s)");

        // Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> addDialog.close());

        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonLayout.add(btnSave, btnCancel);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();

        // Save Mechanism
        btnSave.addClickListener(e -> {
            User newUser = new User();
            newUser.setFirstName(inputFirst.getValue());
            newUser.setLastName(inputLast.getValue());
            ;
            newUser.setEmailAddress(inputEmail.getValue());
            newUser.setPhoneNumber(inputPhone.getValue());
            newUser.setAddress(inputAddress.getValue());
            newUser.setDepartmentId(inputDepartment.getValue());

            if (userService.saveUser(newUser)) {
                addDialog.close();
                updateTable();
            } else {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text("Can't Save User");
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();
            }
        });

        dialogLayout.add(title, divider, scroller, buttonLayout);
        dialogLayout.setWidth("500px");
        dialogLayout.setHeight("500px");
        addDialog.add(dialogLayout);

        return addDialog;
    }

    public Dialog getEditUserDialog(User user) {
        Dialog editDialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        H3 title = new H3("Edit User");
        Hr divider = new Hr();

        divider.setWidthFull();
        divider.setHeight("3px");

        // Input Field
        TextField inputFirst = new TextField("First Name");
        TextField inputLast = new TextField("Last Field");
        HorizontalLayout inputName = new HorizontalLayout(inputFirst, inputLast);
        EmailField inputEmail = new EmailField("Email");
        TextArea inputAddress = new TextArea("Address");
        TextField inputPhone = new TextField("Phone Number");
        new PhoneI18nFieldFormatter(PhoneI18nFieldFormatter.REGION_ID).extend(inputPhone);

        inputName.setWidthFull();
        inputFirst.setWidth("50%");
        inputLast.setWidth("50%");
        inputEmail.setWidthFull();
        inputAddress.setWidthFull();
        inputAddress.setMaxLength(255);
        inputAddress.setHelperText("0/255");
        inputAddress.setValueChangeMode(ValueChangeMode.EAGER);
        inputAddress.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/255");
        });
        inputPhone.setWidthFull();

        ComboBox<Company> inputCompany = new ComboBox<>("Company Name");
        inputCompany.setItems(companyService.getAllCompany());
        inputCompany.setItemLabelGenerator(Company::getCompanyName);
        inputCompany.setWidth("50%");

        ComboBox<Department> inputDepartment = new ComboBox<>("Department Name");
        inputDepartment.setItemLabelGenerator(Department::getName);
        inputDepartment.setWidth("50%");

        HorizontalLayout inputCompDept = new HorizontalLayout(inputCompany, inputDepartment);
        inputCompDept.setWidthFull();

        inputCompany.addValueChangeListener(e -> {
            inputDepartment.clear();
            inputDepartment.setItems(departmentService.getByCompany(e.getValue()));
        });

        Scroller scroller = new Scroller(new Div(inputName, inputEmail, inputAddress, inputPhone, inputCompDept));
        scroller.setHeightFull();
        scroller.setWidthFull();
        scroller.getStyle()
                .set("padding", "var(--lumo-space-s)");

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

        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonLayout.add(btnSave, btnCancel);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();

        // Save Mechanism
        btnSave.addClickListener(e -> {
            User newUser = user;
            newUser.setFirstName(inputFirst.getValue());
            newUser.setLastName(inputLast.getValue());
            ;
            newUser.setEmailAddress(inputEmail.getValue());
            newUser.setPhoneNumber(inputPhone.getValue());
            newUser.setAddress(inputAddress.getValue());
            newUser.setDepartmentId(inputDepartment.getValue());

            if (userService.editData(newUser)) {
                editDialog.close();
                updateTable();
            } else {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text("Can't Save User");
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();
            }
        });

        dialogLayout.add(title, divider, scroller, buttonLayout);
        dialogLayout.setWidth("500px");
        dialogLayout.setHeight("500px");
        editDialog.add(dialogLayout);

        return editDialog;
    }

    public Button getDeleteButton(User user) {
        Button btnDelete = new Button(new Icon(VaadinIcon.TRASH));
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);

        // Create Confirmation Dialog
        Dialog confirmationDialog = new Dialog();
        VerticalLayout confirmationLayout = new VerticalLayout();

        Text confirmationText = new Text("Are you sure?");
        Button confirmationButton = new Button("Yes", e -> {
            userService.deleteUser(user);
            ;
            confirmationDialog.close();
            updateTable();
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
