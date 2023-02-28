package com.adivii.companymanagement.views;

import java.util.stream.Stream;

import javax.servlet.http.HttpSession;

import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.DepartmentService;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.views.dialog.NewPasswordDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
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
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;

@Route("/setting")
public class UserSetting extends HorizontalLayout {
    private UserService userService;
    private DepartmentService departmentService;
    private CompanyService companyService;
    private HttpSession session;

    public UserSetting(UserService userService, DepartmentService departmentService, CompanyService companyService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.companyService = companyService;
        this.session = SessionService.getCurrentSession();

        VerticalLayout sidebar = new SidebarLayout();
        UserSettingMainLayout mainLayout = new UserSettingMainLayout(this.userService);
        Scroller scroller = new Scroller(mainLayout.getLayout());

        sidebar.setWidth("250px");

        setSizeFull();
        add(sidebar, scroller);
    }
}

class UserSettingMainLayout extends VerticalLayout {
    VerticalLayout layout;
    UserService userService;
    HttpSession session;

    User user;

    // Layout for message
    Span message;

    // Layout for name
    TextField inputFirst;
    TextField inputLast;
    HorizontalLayout nameLayout;

    // Layout for address
    TextArea inputAddress;

    // Layout for phone number
    TextField inputPhone;

    // Layout for button
    Button btnSave;
    Button btnChangePass;
    HorizontalLayout buttonLayout;

    public UserSettingMainLayout(UserService userService) {
        this.userService = userService;
        this.session = SessionService.getCurrentSession();

        this.layout = new VerticalLayout();
        this.user = (User) session.getAttribute("userID");
        
        this.message = new Span();

        this.inputFirst = new TextField("First Name");
        this.inputLast = new TextField("Last Name");
        this.nameLayout = new HorizontalLayout(this.inputFirst, this.inputLast);

        this.inputAddress = new TextArea("Address");

        this.inputPhone = new TextField("Phone Number");
        new PhoneI18nFieldFormatter(PhoneI18nFieldFormatter.REGION_ID).extend(this.inputPhone);

        this.btnSave = new Button("Save");
        this.btnChangePass = new Button("Change Password");
        this.buttonLayout = new HorizontalLayout(this.btnSave);

        this.updateField();
    }

    private void updateField() { 

        // TODO: Create message layout (make it interesting)
        // Setting for message
        ErrorService errorService = ((ErrorService) session.getAttribute("errorStatus"));
        message.setVisible(this.user.isActivated());
        message.setText(errorService.getErrorMessage());

        // Setting for name field
        inputFirst.addValueChangeListener(e -> {
            if(!e.getValue().equals(user.getFirstName())){
                btnSave.setEnabled(true);
            } else {
                btnSave.setEnabled(false);
            }
        });

        inputLast.addValueChangeListener(e -> {
            if(!e.getValue().equals(user.getLastName())){
                btnSave.setEnabled(true);
            } else {
                btnSave.setEnabled(false);
            }
        });

        inputFirst.setValue(user.getFirstName());
        inputLast.setValue(user.getLastName());

        // Setting for address
        inputAddress.setWidthFull();
        inputAddress.setMaxLength(255);
        inputAddress.setHelperText("0/255");
        
        inputAddress.setValueChangeMode(ValueChangeMode.EAGER);
        inputAddress.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/255");
            if(!e.getValue().equals(user.getFirstName())){
                btnSave.setEnabled(true);
            } else {
                btnSave.setEnabled(false);
            }
        });

        inputAddress.setValue(user.getAddress());

        // Setting for phone number
        new PhoneI18nFieldFormatter(PhoneI18nFieldFormatter.REGION_ID).extend(inputPhone);
        inputPhone.setWidthFull();

        inputPhone.setValue(user.getPhoneNumber());

        // Setting for button
        btnSave.setEnabled(false);
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSave.addClickListener(e -> {
            user.setFirstName(inputFirst.getValue());
            user.setLastName(inputLast.getValue());
            user.setAddress(inputAddress.getValue());
            user.setPhoneNumber(inputPhone.getValue());

            ErrorService error = userService.editData(user);
            if (error.isErrorStatus()) {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text(error.getErrorMessage());
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();
            } else {
                this.updateField();
            }
        });

        btnChangePass.setEnabled(true);
        btnChangePass.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnChangePass.addClickListener(e -> {
            NewPasswordDialog passwordDialog = new NewPasswordDialog(userService);

            passwordDialog.open();
        });
    }

    public VerticalLayout getLayout() {
        return new VerticalLayout(this.message,
                                    this.nameLayout,
                                    this.inputAddress,
                                    this.inputPhone,
                                    this.btnChangePass,
                                    this.buttonLayout);
    }
}