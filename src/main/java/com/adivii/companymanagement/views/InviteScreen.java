package com.adivii.companymanagement.views;

import java.util.List;
import java.util.Map;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.RoleMap;
import com.adivii.companymanagement.data.service.AccountService;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.RoleMapService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.security.CompanyNameEncoder;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

@Route("/invite")
public class InviteScreen extends VerticalLayout implements HasUrlParameter<String> {
    CompanyService companyService;
    UserService userService;
    AccountService accountService;
    RoleMapService roleMapService;

    String email;
    Company company;

    public InviteScreen(CompanyService companyService, UserService userService, AccountService accountService, RoleMapService roleMapService) {

        this.companyService = companyService;
        this.userService = userService;
        this.accountService = accountService;
        this.roleMapService = roleMapService;
    }

    private void process() {

        if (accountService.getByEmail(email).size() > 0) {
            showConfirmationDialog();
        } else {
            UI.getCurrent().getPage().setLocation("/register?email=" + email);
        }
    }

    private void showConfirmationDialog() {
        // Create Confirmation Dialog
        Dialog confirmationDialog = new Dialog();
        VerticalLayout confirmationLayout = new VerticalLayout();

        Text confirmationText = new Text(
                "You will be added to Company " + this.company.getCompanyName() + ".\nAre you sure?");
        Button confirmationButton = new Button("Yes", e -> {
            RoleMap roleMap = new RoleMap();
            roleMap.setUser(userService.getByEmail(email).get(0));
            roleMap.setCompany(company);

            roleMapService.add(roleMap);
        });
        Button cancelButton = new Button("No", e -> {
            confirmationDialog.close();
        });
        HorizontalLayout buttonLayout = new HorizontalLayout(confirmationButton, cancelButton);

        confirmationDialog.setCloseOnEsc(false);
        confirmationDialog.setCloseOnOutsideClick(false);
        confirmationLayout.add(confirmationText, buttonLayout);
        confirmationLayout.setAlignItems(Alignment.CENTER);
        confirmationDialog.add(confirmationLayout);
        confirmationDialog.open();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String param) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location
                .getQueryParameters();

        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        this.email = parametersMap.get("email").get(0);
        this.company = CompanyNameEncoder.decode(companyService, parametersMap.get("comp").get(0));

        process();
    }
}
