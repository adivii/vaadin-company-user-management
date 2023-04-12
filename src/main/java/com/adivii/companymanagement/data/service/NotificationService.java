package com.adivii.companymanagement.data.service;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * NotificationService
 */
public class NotificationService {

    public static void showNotification(NotificationVariant variant, String message) {
        Notification notification = new Notification();
        notification.addThemeVariants(variant);
        Text notificationText = new Text(message);
        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

        notification.setDuration(5000);
        notification.add(notificationLayout);
        notification.open();
    }
}