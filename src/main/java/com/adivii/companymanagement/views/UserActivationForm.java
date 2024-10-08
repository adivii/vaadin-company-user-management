package com.adivii.companymanagement.views;

import javax.servlet.http.HttpSession;

import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Role;
import com.adivii.companymanagement.data.entity.RoleMap;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.AccountService;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.DepartmentService;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.NotificationService;
import com.adivii.companymanagement.data.service.RoleMapService;
import com.adivii.companymanagement.data.service.RoleService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

@Route("/activate")
public class UserActivationForm extends VerticalLayout {
    // Constant
    private final String METHOD_ADD = "add";
    private final String METHOD_EDIT = "edit";

    // Services
    private AccountService accountService;
    private UserService userService;
    private CompanyService companyService;
    private DepartmentService departmentService;
    private RoleService roleService;
    private RoleMapService roleMapService;

    // Session
    private HttpSession session;

    // User
    private User user;

    // Role Map
    private RoleMap roleMap;

    public UserActivationForm(AccountService accountService, UserService userService,
            CompanyService companyService, DepartmentService departmentService, RoleService roleService,
            RoleMapService roleMapService) {
        this.accountService = accountService;
        this.userService = userService;
        this.companyService = companyService;
        this.departmentService = departmentService;
        this.roleService = roleService;
        this.roleMapService = roleMapService;

        this.session = SessionService.getCurrentSession();
        this.user = this.userService.getUser((Integer) session.getAttribute("userID")).get();

        this.roleMap = this.roleMapService.getByEmail(this.user.getEmail()).get(0);

        validate();
    }

    private void validate() {
        if(checkRequiredForm()) {
            User user = roleMap.getUser();
            user.setActivated(true);
            userService.editData(user);

            session.setAttribute("currentRole", roleMapService.getByEmail(user.getEmail()).get(0));
            
            UI.getCurrent().getPage().setLocation("/");
        }
    }

    // TODO: Implement method to check if there are any null value in either table
    // TODO: Implement method to check if the activation succeded (and activate
    // account also)
    private boolean checkRequiredForm() {
        boolean status = true;

        if (roleMap.getCompany() == null) {
            initiateCompanyForm(METHOD_ADD);
            this.roleMap = this.roleMapService.getByEmail(this.user.getEmail()).get(0);

            status = false;
        } else if (roleMap.getRole() == null) {
            if (roleMap.getCompany() == null) {
                initiateCompanyForm(METHOD_ADD);
            }

            addDefaultRole();
            this.roleMap = this.roleMapService.getByEmail(this.user.getEmail()).get(0);

            status = false;
        } else if (roleMap.getUser().checkIncompleted()) {
            initiateUserForm();
            this.roleMap = this.roleMapService.getByEmail(this.user.getEmail()).get(0);

            status = false;
        } else {
            if (roleMap.getCompany().checkIncompleted()) {
                initiateCompanyForm(METHOD_EDIT);

                status = false;
            }
        }

        return status;
    }

    private void initiateCompanyForm(String method) {
        // Clear Screen
        this.removeAll();

        // Company
        // Name
        TextField companyNameInput;
        // Address
        TextArea companyAddressInput;
        // Webstite
        TextField companyWebsiteInput;
        // Sector
        TextField sectorInput;
        // Button
        Button btnSaveCompany;

        // Name
        companyNameInput = new TextField("Company Name");
        // Address
        companyAddressInput = new TextArea("Company Address");
        companyAddressInput.setMaxLength(255);
        companyAddressInput.setHelperText("0/255");
        companyAddressInput.setValueChangeMode(ValueChangeMode.EAGER);
        companyAddressInput.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/255");
        });
        // Sector
        sectorInput = new TextField("Sector");
        // Website
        companyWebsiteInput = new TextField("Website");

        if (method.equals(METHOD_EDIT)) {
            companyNameInput.setValue(
                    roleMap.getCompany().getCompanyName() == null ? "" : roleMap.getCompany().getCompanyName());
            companyAddressInput
                    .setValue(roleMap.getCompany().getAddress() == null ? "" : roleMap.getCompany().getAddress());
            sectorInput.setValue(roleMap.getCompany().getSector() == null ? "" : roleMap.getCompany().getSector());
            companyWebsiteInput
                    .setValue(roleMap.getCompany().getWebsite() == null ? "" : roleMap.getCompany().getWebsite());
        }

        // Button
        btnSaveCompany = new Button("Save");
        btnSaveCompany.addClickListener(clickEvent -> {
            Company newCompany = method.equals(METHOD_EDIT) ? roleMap.getCompany() : new Company();
            newCompany.setCompanyName(companyNameInput.getValue());
            newCompany.setSector(sectorInput.getValue());
            newCompany.setAddress(companyAddressInput.getValue());
            newCompany.setWebsite(companyWebsiteInput.getValue());

            ErrorService companyErrorService = method.equals(METHOD_EDIT) ? companyService.editData(newCompany)
                    : companyService.addCompany(newCompany);
            if (!companyErrorService.isErrorStatus()) {
                roleMap.setCompany(newCompany);
                roleMapService.add(roleMap);
            } else {
                NotificationService.showNotification(NotificationVariant.LUMO_ERROR,
                        companyErrorService.getErrorMessage());
            }

            validate();
            UI.getCurrent().getPage().reload();
        });

        this.add(companyNameInput, companyAddressInput, sectorInput, companyWebsiteInput, btnSaveCompany);
    }

    private void addDefaultRole() {
        Role newRole = new Role();
        newRole = roleService.getByValue("companyadmin").get(0);
        roleMap.setRole(newRole);
        roleMapService.add(roleMap);

        validate();
        UI.getCurrent().getPage().reload();
    }

    private void initiateUserForm() {
        // Clear Screen
        this.removeAll();

        // User
        User newUser = roleMap.getUser();

        // Name
        TextField firstNameInput;
        TextField lastNameInput;
        HorizontalLayout nameLayout;
        // Phone Number
        TextField phoneInput;
        // Address
        TextArea addressInput;
        // Button
        Button btnSaveUser;

        // Name
        firstNameInput = new TextField("First Name");
        firstNameInput.setValue(newUser.getFirstName() == null ? "" : newUser.getFirstName());
        lastNameInput = new TextField("Last Name");
        lastNameInput.setValue(newUser.getLastName() == null ? "" : newUser.getLastName());
        nameLayout = new HorizontalLayout(firstNameInput, lastNameInput);
        // Phone Number
        phoneInput = new TextField("Phone Number");
        phoneInput.setValue(newUser.getPhoneNumber() == null ? "" : newUser.getPhoneNumber());
        (new PhoneI18nFieldFormatter(PhoneI18nFieldFormatter.REGION_ID)).extend(phoneInput);
        // Address
        addressInput = new TextArea("Address");
        addressInput.setMaxLength(255);
        addressInput.setHelperText("0/255");
        addressInput.setValueChangeMode(ValueChangeMode.EAGER);
        addressInput.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/255");
        });
        addressInput.setValue(newUser.getAddress() == null ? "" : newUser.getAddress());
        // Button
        btnSaveUser = new Button("Save");
        btnSaveUser.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSaveUser.addClickListener(e -> {
            newUser.setFirstName(firstNameInput.getValue());
            newUser.setLastName(lastNameInput.getValue());
            newUser.setPhoneNumber(phoneInput.getValue());
            newUser.setAddress(addressInput.getValue());

            // TODO: Handle Error
            ErrorService userErrorService = userService.editData(newUser);
            if (!userErrorService.isErrorStatus()) {
                NotificationService.showNotification(NotificationVariant.LUMO_SUCCESS, "Success");
            } else {
                NotificationService.showNotification(NotificationVariant.LUMO_ERROR,
                        userErrorService.getErrorMessage());
            }

            validate();
            UI.getCurrent().getPage().reload();
        });

        this.add(nameLayout, phoneInput, addressInput, btnSaveUser);
    }
}