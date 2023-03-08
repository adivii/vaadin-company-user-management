package com.adivii.companymanagement.views.dialog;

import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.entity.Role;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.DepartmentService;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.RoleService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.security.CustomPasswordEncoder;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class UserDataDialog extends Dialog {
    public final static String METHOD_NEW = "new";
    public final static String METHOD_UPDATE = "update";

    private CompanyService companyService;
    private DepartmentService departmentService;
    private UserService userService;
    private RoleService roleService;

    User user;

    VerticalLayout dialogLayout;
    H3 title;
    Hr divider;

    // Name Field
    TextField inputFirst;
    TextField inputLast;
    HorizontalLayout inputName;

    EmailField inputEmail;
    TextArea inputAddress;
    TextField inputPhone;

    // Company and Dept
    ComboBox<Company> inputCompany;
    ComboBox<Department> inputDepartment;
    HorizontalLayout inputCompDept;

    ComboBox<Role> inputRole;

    // Button
    HorizontalLayout buttonLayout;
    Button btnSave;
    Button btnCancel;

    Scroller scroller;

    public UserDataDialog(CompanyService companyService, DepartmentService departmentService,
            UserService userService, RoleService roleService, String method) {
        this.companyService = companyService;
        this.departmentService = departmentService;
        this.userService = userService;
        this.roleService = roleService;

        this.initiateDialog(method);
    }

    private void initiateDialog(String method) {
        this.dialogLayout = new VerticalLayout();
        this.title = new H3("Add New User");
        this.divider = new Hr();

        this.divider.setWidthFull();
        this.divider.setHeight("3px");

        // Name Field
        this.inputFirst = new TextField("First Name");
        this.inputLast = new TextField("Last Field");
        this.inputName = new HorizontalLayout(this.inputFirst, this.inputLast);

        this.inputName.setWidthFull();
        this.inputFirst.setWidth("50%");
        this.inputLast.setWidth("50%");

        // Email Input
        this.inputEmail = new EmailField("Email");
        this.inputEmail.setWidthFull();

        // Address Input
        this.inputAddress = new TextArea("Address");

        this.inputAddress.setWidthFull();
        this.inputAddress.setMaxLength(255);
        this.inputAddress.setHelperText("0/255");
        this.inputAddress.setValueChangeMode(ValueChangeMode.EAGER);
        this.inputAddress.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/255");
        });

        // Phone Number Input
        this.inputPhone = new TextField("Phone Number");
        (new PhoneI18nFieldFormatter(PhoneI18nFieldFormatter.REGION_ID)).extend(this.inputPhone);
        this.inputPhone.setWidthFull();

        // Company and Department Input
        this.inputCompany = new ComboBox<>("Company Name");
        this.inputDepartment = new ComboBox<>("Department Name");
        this.inputCompDept = new HorizontalLayout(inputCompany, inputDepartment);

        this.inputCompany.setItems(this.companyService.getAllCompany());
        this.inputCompany.setItemLabelGenerator(Company::getCompanyName);
        this.inputCompany.setWidth("50%");

        this.inputDepartment.setItemLabelGenerator(Department::getName);
        this.inputDepartment.setWidth("50%");

        this.inputCompDept.setWidthFull();

        // Role
        this.inputRole = new ComboBox<>("Role");
        this.inputRole.setItems(roleService.getAllRole());
        this.inputRole.setItemLabelGenerator(Role::getName);
        this.inputRole.setWidthFull();

        this.inputCompany.addValueChangeListener(e -> {
            this.inputDepartment.clear();
            this.inputDepartment.setItems(departmentService.getByCompany(e.getValue()));
        });

        this.scroller = new Scroller(
                new Div(this.inputName, this.inputEmail, this.inputAddress, this.inputPhone, this.inputCompDept,
                        this.inputRole));
        this.scroller.setHeightFull();
        this.scroller.setWidthFull();
        this.scroller.getStyle()
                .set("padding", "var(--lumo-space-s)");

        // Button Layout
        this.buttonLayout = new HorizontalLayout();
        this.btnSave = new Button("Save");
        this.btnCancel = new Button("Cancel", e -> this.close());

        this.btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        this.buttonLayout.add(this.btnSave, btnCancel);
        this.buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        this.buttonLayout.setWidthFull();

        // Save Mechanism
        this.btnSave.addClickListener(e -> {
            User newUser = new User();
            newUser.setFirstName(this.inputFirst.getValue());
            newUser.setLastName(this.inputLast.getValue());
            newUser.setEmailAddress(this.inputEmail.getValue());
            newUser.setPhoneNumber(this.inputPhone.getValue());
            newUser.setAddress(this.inputAddress.getValue());
            newUser.setDepartmentId(this.inputDepartment.getValue());
            newUser.setRole(this.inputRole.getValue());
            newUser.setEnabled(true);

            ErrorService errorService = new ErrorService(false, null);
            if (method.equals("new")) {
                newUser.setPassword((new CustomPasswordEncoder()).encode("password"));
                newUser.setActivated(false);
                errorService = userService.saveUser(newUser);
            } else if (method.equals("update")) {
                newUser.setUserId(this.user.getUserId());
                newUser.setPassword(this.user.getPassword());
                newUser.setActivated(this.user.isActivated());
                errorService = userService.editData(newUser);
            }

            if (!errorService.isErrorStatus()) {
                this.close();
            } else {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text(errorService.getErrorMessage());
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();
            }
        });

        this.dialogLayout.add(this.title, this.divider, this.scroller, this.buttonLayout);
        this.dialogLayout.setWidth("500px");
        this.dialogLayout.setHeight("500px");
        this.add(this.dialogLayout);
    }

    public void setData(User user) {
        this.user = user;
        this.inputFirst.setValue(user.getFirstName());
        this.inputLast.setValue(user.getLastName());
        this.inputEmail.setValue(user.getEmailAddress());
        this.inputAddress.setValue(user.getAddress());
        this.inputPhone.setValue(user.getPhoneNumber());
        this.inputCompany.setValue(user.getDepartmentId().getCompanyId());
        this.inputDepartment.setValue(user.getDepartmentId());
        this.inputRole.setValue(user.getRole());
    }
}
