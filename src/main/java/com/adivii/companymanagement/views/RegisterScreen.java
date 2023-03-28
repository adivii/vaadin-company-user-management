package com.adivii.companymanagement.views;

import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

import com.adivii.companymanagement.data.entity.Account;
import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Role;
import com.adivii.companymanagement.data.entity.RoleMap;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.AccountService;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.DepartmentService;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.RoleMapService;
import com.adivii.companymanagement.data.service.RoleService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.security.CustomPasswordEncoder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

// TODO: Handle Success and Error
// TODO: Show Notification

@Route("/register")
public class RegisterScreen extends VerticalLayout {

    // Services
    private UserService userService;
    private AccountService accountService;
    private CompanyService companyService;
    private DepartmentService departmentService;
    private RoleService roleService;
    private RoleMapService roleMapService;

    // Text Field
    // User
    // Name
    private TextField firstNameInput;
    private TextField lastNameInput;
    private HorizontalLayout nameLayout;
    // Account (Email and Password)
    private EmailField emailInput;
    private PasswordField passInput;
    private PasswordField rePassInput;
    private HorizontalLayout passLayout;
    // Phone Number
    private TextField phoneInput;
    // Address
    private TextArea addressInput;
    // Button
    private Button btnSaveUser;

    // Company
    // Name
    private TextField companyNameInput;
    // Address
    private TextArea companyAddressInput;
    // Webstite
    private TextField companyWebsiteInput;
    // Sector
    private TextField sectorInput;
    // Button
    private Button btnSaveCompany;

    public RegisterScreen(UserService userService, AccountService accountService,
            CompanyService companyService, DepartmentService departmentService, RoleService roleService,
            RoleMapService roleMapService) {
        this.userService = userService;
        this.accountService = accountService;
        this.companyService = companyService;
        this.departmentService = departmentService;
        this.roleMapService = roleMapService;
        this.roleService = roleService;

        setAlignItems(Alignment.CENTER);
        add(initiateUserForm());
    }

    private VerticalLayout initiateUserForm() {
        VerticalLayout accountForm = new VerticalLayout();

        // Name
        firstNameInput = new TextField("First Name");
        lastNameInput = new TextField("Last Name");
        nameLayout = new HorizontalLayout(firstNameInput, lastNameInput);
        // Account (Email and Password)
        emailInput = new EmailField("Email Address");
        passInput = new PasswordField("Password");
        rePassInput = new PasswordField("Confirm Password");
        passLayout = new HorizontalLayout(passInput, rePassInput);
        // Phone Number
        phoneInput = new TextField("Phone Number");
        (new PhoneI18nFieldFormatter(PhoneI18nFieldFormatter.REGION_ID)).extend(phoneInput);
        // Address
        addressInput = new TextArea("Address");
        addressInput.setMaxLength(255);
        addressInput.setHelperText("0/255");
        addressInput.setValueChangeMode(ValueChangeMode.EAGER);
        addressInput.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/255");
        });
        // Button
        btnSaveUser = new Button("Save");
        btnSaveUser.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSaveUser.addClickListener(e -> {
            // TODO: Implement user save mechanism
            CustomPasswordEncoder encoder = new CustomPasswordEncoder();
            Account newAccount = new Account();

            newAccount.setEmailAddress(emailInput.getValue());
            newAccount.setPassword(encoder.encode(passInput.getValue()));

            ErrorService errorService = accountService.save(newAccount);
            if(!errorService.isErrorStatus()){
                newAccount = accountService.getByEmail(newAccount.getEmailAddress()).get(0);
                User newUser = new User();
                newUser.setFirstName(firstNameInput.getValue());
                newUser.setLastName(lastNameInput.getValue());
                newUser.setEmail(emailInput.getValue());
                newUser.setPhoneNumber(phoneInput.getValue());
                newUser.setAddress(addressInput.getValue());
                newUser.setAccount(newAccount);
                newUser.setEnabled(true);
                newUser.setActivated(true);

                // TODO: Handle Error
                ErrorService userErrorService = userService.saveUser(newUser);
                if(!userErrorService.isErrorStatus()){
                    this.removeAll();
                    this.add(initiateCompanyForm());

                    btnSaveCompany.addClickListener(saveCompanyEvent -> {
                        User ownerUser = userService.getByEmail(newUser.getEmail()).get(0);
                        
                        Company newCompany = new Company();
                        newCompany.setCompanyName(companyNameInput.getValue());
                        newCompany.setSector(sectorInput.getValue());
                        newCompany.setAddress(companyAddressInput.getValue());
                        newCompany.setWebsite(companyWebsiteInput.getValue());

                        ErrorService companyErrorService = companyService.addCompany(newCompany);
                        if(companyErrorService.isErrorStatus()) {
                            newCompany = companyService.getByName(newCompany.getCompanyName()).get(0);
                            Role defaultRole = roleService.getByValue("companyadmin").get(0);
                            
                            RoleMap newRole = new RoleMap();
                            newRole.setCompany(newCompany);
                            newRole.setRole(defaultRole);
                            newRole.setUser(ownerUser);

                            roleMapService.add(newRole);
                        }
                    });
                }
            }
        });

        accountForm.add(nameLayout, emailInput, passLayout, phoneInput, addressInput, btnSaveUser);
        accountForm.setAlignSelf(Alignment.CENTER);
        return accountForm;
    }

    private VerticalLayout initiateCompanyForm() {
        VerticalLayout companyForm = new VerticalLayout();

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
        // Button
        btnSaveCompany = new Button("Save");

        companyForm.add(companyNameInput, companyAddressInput, sectorInput, companyWebsiteInput, btnSaveCompany);
        return companyForm;
    }
}
