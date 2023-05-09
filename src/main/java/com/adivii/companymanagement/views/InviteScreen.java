package com.adivii.companymanagement.views;

import java.util.List;
import java.util.Map;

import com.adivii.companymanagement.data.entity.Account;
import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Invitation;
import com.adivii.companymanagement.data.entity.RoleMap;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.AccountService;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.InvitationService;
import com.adivii.companymanagement.data.service.NotificationService;
import com.adivii.companymanagement.data.service.RoleMapService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.security.CompanyNameEncoder;
import com.adivii.companymanagement.views.component.dialog.NewPasswordDialog;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.NotificationVariant;
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
    InvitationService invitationService;

    String inviteId;
    List<Invitation> inviteList;

    public InviteScreen(CompanyService companyService, UserService userService, AccountService accountService,
            RoleMapService roleMapService, InvitationService invitationService) {

        this.companyService = companyService;
        this.userService = userService;
        this.accountService = accountService;
        this.roleMapService = roleMapService;
        this.invitationService = invitationService;
    }

    private void process() {

        if (invitationService.getByInviteId(inviteId).size() > 0) {
            Invitation invite;
            invite = invitationService.getByInviteId(inviteId).get(0);

            this.inviteList = invitationService.getByEmailAndCompanyAndDepartment(invite.getEmail(),
                    invite.getCompany(), invite.getDepartment());

            for (Invitation invitation : inviteList) {
                showConfirmationDialog(invitation);
            }
        } else {
            NotificationService.showNotification(NotificationVariant.LUMO_ERROR, "Can't find your invitation", 10);
        }
    }

    private void showConfirmationDialog(Invitation invitation) {
        // Create Confirmation Dialog
        Dialog confirmationDialog = new Dialog();
        VerticalLayout confirmationLayout = new VerticalLayout();

        Text confirmationText = new Text(
                "You will be added to Company " + invitation.getCompany().getCompanyName() + " as "
                        + invitation.getRole().getName() + ".\nAre you sure?");
        Button confirmationButton = new Button("Yes", e -> {

            if (invitation.getType().equals(InvitationService.TYPE_NEW)) {
                Account account = new Account();
                account.setEmailAddress(invitation.getEmail());
                NewPasswordDialog passwordDialog = new NewPasswordDialog(accountService, account);
                passwordDialog.open();

                passwordDialog.addOpenedChangeListener(closed -> {
                    if (accountService.getByEmail(invitation.getEmail()).size() > 0) {
                        Account newAccount = accountService.getByEmail(invitation.getEmail()).get(0);

                        User newUser = new User();
                        newUser.setEmail(invitation.getEmail());
                        newUser.setAccount(newAccount);

                        ErrorService userError = userService.saveUser(newUser);
                        if (userError.isErrorStatus()) {
                            NotificationService.showNotification(NotificationVariant.LUMO_ERROR, userError.getErrorMessage());
                        } else {
                            RoleMap newRoleMap = new RoleMap();
                            newRoleMap.setUser(newUser);
                            newRoleMap.setRole(invitation.getRole());
                            newRoleMap.setCompany(invitation.getCompany());
                            newRoleMap.setDepartment(invitation.getDepartment());

                            roleMapService.add(newRoleMap);
                            invitationService.deleteInvitation(invitation);
                            UI.getCurrent().getPage().setLocation("/login");
                        }

                        confirmationDialog.close();
                    }
                });
            }

            // RoleMap roleMap = new RoleMap();
            // roleMap.setUser(userService.getByEmail(email).get(0));
            // roleMap.setCompany(company);

            // roleMapService.add(roleMap);
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
        this.inviteId = parametersMap.get("inviteID").get(0);

        process();
    }
}
