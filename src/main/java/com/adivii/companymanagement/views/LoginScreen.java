package com.adivii.companymanagement.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("")
@PageTitle("Login Screen")
public class LoginScreen extends VerticalLayout {
    UserService userService;
    VaadinSession session;

    public LoginScreen(UserService userService) {
        this.userService = userService;
        session = VaadinSession.getCurrent();

        // Redirect if user already login
        if(session.getAttribute("userID") != null) {
            UI.getCurrent().getPage().setLocation("/company");
        }

        EmailField emailField = new EmailField("Email Address");
        PasswordField passField = new PasswordField("Password");
        Button btnLogin = new Button("Login");
        btnLogin.addClickShortcut(Key.ENTER);

        btnLogin.addClickListener(e -> {
            User user = new User();
            user.setEmailAddress(emailField.getValue());
            user.setPassword(passField.getValue());

            ErrorService errorService = userService.validateUser(user);

            if(!errorService.isErrorStatus()) {
                User loggedUser = userService.getByEmail(emailField.getValue()).get(0);
                session.setAttribute("userID", loggedUser);
                session.getSession().setMaxInactiveInterval(1800); // Inactive Interval in Second(s)
                getUI().ifPresent(ui -> ui.navigate(CompanyList.class));
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
        
        add(emailField, passField, btnLogin);
        // if(session.getAttribute("userID") != null) {
        //     System.out.println(((User) session.getAttribute("userID")).getRole());
        // }   
    }
}
