package com.adivii.companymanagement.data.configuration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionDestroyListener;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletConfiguration;
import com.vaadin.flow.server.VaadinServletRequest;

@WebServlet(urlPatterns = "/*", name = "myservlet", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false, heartbeatInterval = 10)
public class ServletConfig extends VaadinServlet implements SessionInitListener, SessionDestroyListener {

    @Override
    protected void servletInitialized() throws ServletException {
        // TODO Auto-generated method stub
        super.servletInitialized();
        getService().addSessionInitListener(this);
    }

    @Override
    public void sessionInit(SessionInitEvent arg0) throws ServiceException {
        // TODO Auto-generated method stub
        System.out.println("Session Created");
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent arg0) {
        // TODO Auto-generated method stub
        Dialog dialog = new Dialog();
        HorizontalLayout dialogLayout = new HorizontalLayout();
        Label label = new Label("Your Session Has Been Expired");
        Button button = new Button("Close");

        button.addThemeVariants(ButtonVariant.LUMO_SMALL);
        button.addClickListener(e -> {
            UI.getCurrent().getPage().setLocation("/");
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(
                    VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                    null);
        });

        dialogLayout.setAlignItems(Alignment.CENTER);
        dialogLayout.add(label, button);

        dialog.add(dialogLayout);
        dialog.open();
    }
}
