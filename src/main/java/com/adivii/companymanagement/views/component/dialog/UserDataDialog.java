package com.adivii.companymanagement.views.component.dialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.entity.Role;
import com.adivii.companymanagement.data.entity.RoleMap;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.AccountService;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.DepartmentService;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.MailSenderService;
import com.adivii.companymanagement.data.service.NotificationService;
import com.adivii.companymanagement.data.service.RoleMapService;
import com.adivii.companymanagement.data.service.RoleService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
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
    private RoleMapService roleMapService;
    private AccountService accountService;
    private HttpSession session;

    User user;
    User currentUser;

    RoleMap currentRole;
    List<Role> roleList;

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

    CheckboxGroup<Role> inputRole;

    // Button
    HorizontalLayout buttonLayout;
    Button btnSave;
    Button btnCancel;

    Scroller scroller;

    public UserDataDialog(CompanyService companyService, DepartmentService departmentService,
            UserService userService, RoleService roleService, RoleMapService roleMapService,
            AccountService accountService, String method) {
        this.companyService = companyService;
        this.departmentService = departmentService;
        this.userService = userService;
        this.roleService = roleService;
        this.roleMapService = roleMapService;

        this.session = SessionService.getCurrentSession();

        if (this.session.getAttribute("userID") != null) {
            currentUser = userService.getUser((Integer) this.session.getAttribute("userID")).get();
        }

        if (this.session.getAttribute("currentRole") != null) {
            currentRole = (RoleMap) session.getAttribute("currentRole");
        }

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

        List<Company> companyItems = new ArrayList<>();
        if (currentRole.getCompany().getHoldingCompany() == null) {
            companyItems
                    .addAll(this.companyService.getByName(currentRole.getCompany().getCompanyName()));
            companyItems.addAll(this.companyService.getChildCompany(currentRole.getCompany()));
        } else {
            companyItems.addAll(this.companyService.getChildCompany(
                    currentRole.getCompany().getHoldingCompany()));
        }
        this.inputCompany.setItems(companyItems);
        this.inputCompany.setItemLabelGenerator(Company::getCompanyName);
        this.inputCompany.setWidth("50%");

        this.inputDepartment.setItemLabelGenerator(Department::getName);
        this.inputDepartment.setWidth("50%");

        this.inputCompDept.setWidthFull();

        // Role
        roleList = roleService.getAllRole();
        this.inputRole = new CheckboxGroup<>();
        this.inputRole.setLabel("Role");
        this.inputRole.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        this.inputRole.setItems(roleList);
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
        // TODO: Configure saving method to save both Account and User
        // TODO: Adapt saving method to process RoleMap
        this.btnSave.addClickListener(e -> {
            User newUser = new User();
            newUser.setFirstName(inputFirst.getValue());
            newUser.setLastName(inputLast.getValue());
            newUser.setEmail(inputEmail.getValue());
            newUser.setPhoneNumber(inputPhone.getValue());
            newUser.setAddress(inputAddress.getValue());
            // newUser.setAccount(newAccount);
            newUser.setEnabled(true);
            // newUser.setActivated(true);

            ErrorService errorService = new ErrorService(false, null);
            if (method.equals("new")) {
                // newUser.setPassword((new CustomPasswordEncoder()).encode("password"));
                newUser.setActivated(false);
                errorService = userService.saveUser(newUser);
            } else if (method.equals("update")) {
                newUser.setUserId(this.user.getUserId());
                newUser.setAccount(user.getAccount());
                newUser.setAvatar(user.getAvatar());
                // newUser.setPassword(this.user.getPassword());
                newUser.setActivated(this.user.isActivated());
                errorService = userService.editData(newUser);
            }

            // TODO: Handle Error to rollback changes if failed to save data
            // TODO: Save only master entity, child entity should be updated (or use thread)
            if (!errorService.isErrorStatus()) {
                if (inputRole.getValue().size() < 1) {
                    NotificationService.showNotification(NotificationVariant.LUMO_ERROR,
                            "User Must Have At Least One Role");
                } else {
                    for (Role role : roleService.getAllRole()) {
                        RoleMap roleMap = new RoleMap();

                        if (roleMapService.getByEmailAndRoleAndCompanyAndDepartment(newUser.getEmail(),
                                role,
                                inputCompany.getValue(),
                                inputDepartment.getValue()).size() > 0) {
                            roleMap = roleMapService.getByEmailAndRoleAndCompanyAndDepartment(newUser.getEmail(),
                                    role,
                                    inputCompany.getValue(),
                                    inputDepartment.getValue()).get(0);

                            if (new ArrayList<>(inputRole.getValue()).contains(role)) {
                                roleMap.setCompany(inputCompany.getValue());
                                roleMap.setDepartment(inputDepartment.getValue());
                                roleMap.setRole(role);
                                roleMap.setUser(newUser);
                                roleMapService.add(roleMap);
                            } else {
                                roleMapService.delete(roleMap);
                            }
                        } else {
                            if (new ArrayList<>(inputRole.getValue()).contains(role)) {
                                roleMap.setCompany(inputCompany.getValue());
                                roleMap.setDepartment(inputDepartment.getValue());
                                roleMap.setRole(role);
                                roleMap.setUser(newUser);
                                roleMapService.add(roleMap);
                            }
                        }
                    }

                    if (method == this.METHOD_NEW) {
                        MailSenderService.sendEmail(newUser.getEmail(), "Invitation", "You have been registered at company ".concat(inputCompany.getValue().getCompanyName()));
                    }

                    this.close();
                }
            } else {
                NotificationService.showNotification(NotificationVariant.LUMO_ERROR, errorService.getErrorMessage());
            }
        });

        this.dialogLayout.add(this.title, this.divider, this.scroller, this.buttonLayout);
        this.dialogLayout.setWidth("500px");
        this.dialogLayout.setHeight("500px");

        if (method.equals(this.METHOD_UPDATE)) {
            setData(currentUser);
        }

        this.add(this.dialogLayout);
    }

    public void setData(User user) {
        this.user = user;
        this.inputFirst.setValue(user.getFirstName());
        this.inputLast.setValue(user.getLastName());
        this.inputEmail.setValue(user.getEmail());
        this.inputAddress.setValue(user.getAddress());
        this.inputPhone.setValue(user.getPhoneNumber());
        this.inputCompany.setValue(currentRole.getCompany());
        this.inputDepartment.setValue(currentRole.getDepartment());

        List<Role> ownedRole = new ArrayList<>();
        for (RoleMap roleMap : currentUser.getRoleId()) {
            ownedRole.add(roleMap.getRole());
        }

        for (Role role : roleList) {
            if (ownedRole.contains(role)) {
                this.inputRole.select(role);
            }
        }
    }
}
