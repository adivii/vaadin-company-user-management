package com.adivii.companymanagement.views;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import org.springframework.mail.javamail.JavaMailSender;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.RoleMap;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.AccountService;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.DepartmentService;
import com.adivii.companymanagement.data.service.InvitationService;
import com.adivii.companymanagement.data.service.MailSenderService;
import com.adivii.companymanagement.data.service.NotificationService;
import com.adivii.companymanagement.data.service.RoleMapService;
import com.adivii.companymanagement.data.service.RoleService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.filter.UserFilterService;
import com.adivii.companymanagement.data.service.security.CompanyNameEncoder;
import com.adivii.companymanagement.views.component.CustomAvatar;
import com.adivii.companymanagement.views.component.dialog.UserDataDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

// TODO: Fetch data by RoleMap
// TODO: or modify program so that we can show multiple role in one account
@Route("/user")
@PageTitle("User List")
public class UserList extends HorizontalLayout implements BeforeEnterObserver {
        User currentUser;
        RoleMap currentRole;

        UserService userService;
        CompanyService companyService;
        DepartmentService departmentService;
        UserFilterService userFilterService;
        RoleService roleService;
        RoleMapService roleMapService;
        AccountService accountService;
        InvitationService invitationService;
        HttpSession session;

        JavaMailSender mailSender;

        Grid<User> userTable;

        public UserList(UserService userService, CompanyService companyService, DepartmentService departmentService,
                        RoleService roleServices, RoleMapService roleMapService, AccountService accountService,
                        InvitationService invitationService, JavaMailSender mailSender) {
                this.userService = userService;
                this.companyService = companyService;
                this.departmentService = departmentService;
                this.roleMapService = roleMapService;
                this.userFilterService = new UserFilterService();
                this.roleService = roleServices;
                this.accountService = accountService;
                this.invitationService = invitationService;

                this.mailSender = mailSender;

                this.session = SessionService.getCurrentSession();

                if (this.session.getAttribute("userID") != null) {
                        currentUser = userService.getUser((Integer) this.session.getAttribute("userID")).get();
                }

                if (this.session.getAttribute("currentRole") != null) {
                        currentRole = (RoleMap) this.session.getAttribute("currentRole");
                }

                VerticalLayout sidebar = new SidebarLayout(this.userService);

                sidebar.setWidth("250px");

                setSizeFull();
                add(sidebar, getLayout());
        }

        public Grid<User> getUserTable() {
                this.userTable = new Grid<>(User.class, false);

                this.userTable.addComponentColumn(e -> {
                        Button button = getDeleteButton(e);

                        return button;
                }).setWidth("75px").setFlexGrow(0).setFrozen(true);
                this.userTable.addComponentColumn(e -> {
                        CustomAvatar avatar = new CustomAvatar(e.getFirstName().concat(" ").concat(e.getLastName()));
                        avatar.setColor(((int) e.getFirstName().charAt(0) + (int) e.getLastName().charAt(0)) % 4);

                        if (e.getAvatar() != null) {
                                avatar.setAvatar(new Image(new StreamResource("profile", () -> {
                                        InputStream profileStream;
                                        try {
                                                profileStream = new URL(e.getAvatar().getUri()).openStream();
                                                return profileStream;
                                        } catch (MalformedURLException e1) {
                                                // TODO Auto-generated catch block
                                                e1.printStackTrace();
                                                return null;
                                        } catch (IOException e1) {
                                                // TODO Auto-generated catch block
                                                e1.printStackTrace();
                                                return null;
                                        }
                                }), null));
                        }

                        return avatar;
                }).setWidth("70px").setFlexGrow(0).setFrozen(true);
                // TODO : Apply Filter Header
                Grid.Column<User> emailColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                                "<span title='[[item.email]]' aria-label='[[item.email]]'>[[item.email]]</span>")
                                .withProperty("email", User::getEmail)).setAutoWidth(true).setResizable(true)
                                .setFrozen(true);
                Grid.Column<User> firstColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                                "<span title='[[item.first]]' aria-label='[[item.first]]'>[[item.first]]</span>")
                                .withProperty("first", User::getFirstName)).setAutoWidth(true)
                                .setResizable(true);
                Grid.Column<User> lastColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                                "<span title='[[item.last]]' aria-label='[[item.last]]'>[[item.last]]</span>")
                                .withProperty("last", User::getLastName)).setAutoWidth(true).setResizable(true);
                Grid.Column<User> addressColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                                "<span title='[[item.address]]' aria-label='[[item.address]]'>[[item.address]]</span>")
                                .withProperty("address", User::getAddress)).setAutoWidth(true).setResizable(true);
                Grid.Column<User> phoneColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                                "<span title='[[item.phone]]' aria-label='[[item.phone]]'>[[item.phone]]</span>")
                                .withProperty("phone", User::getPhoneNumber)).setAutoWidth(true).setResizable(true);
                Grid.Column<User> companyColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                                "<span title='[[item.company]]' aria-label='[[item.company]]'>[[item.company]]</span>")
                                .withProperty("company", e -> {
                                        if (roleMapService.getByEmail(e.getEmail()).size() == 0) {
                                                return "";
                                        } else {
                                                if (roleMapService.getByEmail(e.getEmail()).get(0)
                                                                .getCompany() != null) {
                                                        return roleMapService.getByEmail(e.getEmail()).get(0)
                                                                        .getCompany()
                                                                        .getCompanyName();
                                                } else {
                                                        return "";
                                                }
                                        }
                                }))
                                .setAutoWidth(true).setResizable(true);
                Grid.Column<User> departmentColumn = this.userTable.addColumn(TemplateRenderer.<User>of(
                                "<span title='[[item.department]]' aria-label='[[item.department]]'>[[item.department]]</span>")
                                .withProperty("department", e -> {
                                        if (roleMapService.getByEmail(e.getEmail()).size() == 0) {
                                                return "";
                                        } else {
                                                if (roleMapService.getByEmail(e.getEmail()).get(0)
                                                                .getDepartment() == null) {
                                                        return "";
                                                } else {
                                                        return roleMapService.getByEmail(e.getEmail()).get(0)
                                                                        .getDepartment().getName();
                                                }
                                        }
                                }))
                                .setAutoWidth(true).setResizable(true);

                this.userTable.addItemDoubleClickListener(e -> {
                        UserDataDialog userDataDialog = new UserDataDialog(companyService, departmentService,
                                        userService, roleService, roleMapService, accountService, invitationService, mailSender,
                                        UserDataDialog.METHOD_UPDATE);
                        userDataDialog.setData(e.getItem());

                        userDataDialog.open();

                        userDataDialog.addOpenedChangeListener(actionListener -> {
                                updateTable();
                        });
                });

                this.userTable.setHeightFull();

                updateTable();

                this.userTable.getHeaderRows().clear();
                HeaderRow headerRow = this.userTable.appendHeaderRow();

                headerRow.getCell(emailColumn).setComponent(
                                createFilterHeader("Email", this.userFilterService::setEmail));
                headerRow.getCell(firstColumn).setComponent(
                                createFilterHeader("First Name", this.userFilterService::setFirstName));
                headerRow.getCell(lastColumn).setComponent(
                                createFilterHeader("Last Name", this.userFilterService::setLastName));
                headerRow.getCell(addressColumn).setComponent(
                                createFilterHeader("Address", this.userFilterService::setAddress));
                headerRow.getCell(phoneColumn).setComponent(
                                createFilterHeader("Phone", this.userFilterService::setPhone));
                headerRow.getCell(companyColumn).setComponent(
                                createFilterHeader("Company", this.userFilterService::setCompany));
                headerRow.getCell(departmentColumn).setComponent(
                                createFilterHeader("Department", this.userFilterService::setDepartment));

                return this.userTable;
        }

        private static Component createFilterHeader(String labelText,
                        Consumer<String> filterChangeConsumer) {
                Label label = new Label(labelText);
                label.getStyle().set("padding-top", "var(--lumo-space-m)")
                                .set("font-size", "var(--lumo-font-size-xs)");
                TextField textField = new TextField();
                textField.setValueChangeMode(ValueChangeMode.EAGER);
                textField.setClearButtonVisible(true);
                textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
                textField.setWidthFull();
                textField.getStyle().set("max-width", "100%");
                textField.addValueChangeListener(
                                e -> filterChangeConsumer.accept(e.getValue()));
                VerticalLayout layout = new VerticalLayout(label, textField);
                layout.getThemeList().clear();
                layout.getThemeList().add("spacing-xs");

                return layout;
        }

        public void updateTable() {
                // TODO: Can't update table when add new record (if using only refreshAll)
                ListDataProvider<User> provider;
                List<User> userList = new ArrayList<>();
                List<Company> compList = new ArrayList<>();

                if (currentRole.getRole().getValue().equals("companyadmin")) {
                        if (currentRole.getCompany().getHoldingCompany() == null) {
                                compList.addAll(this.companyService.getByName(currentRole
                                                .getCompany().getCompanyName()));
                                compList.addAll(this.companyService
                                                .getChildCompany(currentRole
                                                .getCompany()));
                        } else {
                                compList.addAll(this.companyService.getChildCompany(
                                                currentRole.getCompany().getHoldingCompany()));
                        }

                        for (Company comp : compList) {
                                for (User user : userService.getByCompany(comp)) {
                                        if (!userList.contains(user)) {
                                                userList.add(user);
                                        }
                                }
                        }

                        provider = new ListDataProvider<>(userList);
                } else if (currentRole.getRole().getValue().equals("departmentadmin")) {
                        List<User> users = new ArrayList<>();
                        for (User user : userService.getByDepartment(currentRole.getDepartment())) {
                                if(!users.contains(user)){
                                        users.add(user);
                                }
                        }
                        provider = new ListDataProvider<>(users);
                } else {
                        provider = new ListDataProvider<>(new ArrayList<>());
                }

                // userTable.setItems(items);
                userTable.setDataProvider(provider);
                userFilterService.setDataProvider(provider);
        }

        public VerticalLayout getLayout() {
                VerticalLayout mainLayout = new VerticalLayout();
                Button btnAdd = new Button("Add New User");
                Button btnInvite = new Button("Invite User");
                TextField searchBox = new TextField();
                HorizontalLayout searchLayout = new HorizontalLayout(btnAdd, btnInvite, searchBox);

                searchBox.setPlaceholder("Search");
                searchBox.setValueChangeMode(ValueChangeMode.EAGER);
                searchBox.addValueChangeListener(e -> {
                        this.userFilterService.setSearchTerm(e.getValue());

                        searchBox.setClearButtonVisible(!searchBox.getValue().isEmpty());
                });

                getUserTable();

                btnAdd.addClickListener(e -> {
                        UserDataDialog userDialog = new UserDataDialog(this.companyService, this.departmentService,
                                        this.userService, roleService, roleMapService, accountService,
                                        invitationService, mailSender, UserDataDialog.METHOD_NEW);
                        userDialog.open();

                        userDialog.addOpenedChangeListener(actionListener -> {
                                updateTable();
                        });
                });

                btnInvite.addClickListener(e -> {
                        showInvitationDialog();
                });

                mainLayout.add(searchLayout, this.userTable);
                mainLayout.setHeightFull();

                return mainLayout;
        }

        private void showInvitationDialog() {
                Dialog dialog = new Dialog();
                VerticalLayout dialogLayout = new VerticalLayout();

                TextField emailInput = new TextField("Email");
                Button btnSend = new Button("Send");
                Button btnCancel = new Button("Cancel");
                HorizontalLayout buttonLayout = new HorizontalLayout(btnSend, btnCancel);

                dialog.setCloseOnEsc(false);
                dialog.setCloseOnOutsideClick(false);

                btnSend.addClickListener(e -> {
                        if(userService.getByEmail(emailInput.getValue()).size() == 0) {
                                NotificationService.showNotification(NotificationVariant.LUMO_ERROR, "Can't find user!");
                        } else {
                                try {
                                        MailSenderService.sendEmail(mailSender, emailInput.getValue(), "Invitation", "http://localhost:8080/invite?email=".concat(emailInput.getValue()).concat("&comp=").concat(CompanyNameEncoder.encode(currentRole.getCompany())));
                                        dialog.close();
                                } catch (UnsupportedEncodingException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                } catch (MessagingException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                }
                        }
                });
                btnCancel.addClickListener(e -> dialog.close());

                dialogLayout.add(emailInput, buttonLayout);
                dialog.add(dialogLayout);
                dialog.open();
        }

        public Button getDeleteButton(User user) {
                Button btnDelete = new Button(new Icon(VaadinIcon.TRASH));
                btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR,
                                ButtonVariant.LUMO_SMALL);

                // Create Confirmation Dialog
                Dialog confirmationDialog = new Dialog();
                VerticalLayout confirmationLayout = new VerticalLayout();

                Text confirmationText = new Text("Are you sure?");
                Button confirmationButton = new Button("Yes", e -> {
                        if (user.equals(currentUser)) {
                                NotificationService.showNotification(NotificationVariant.LUMO_ERROR, "You can't delete yourself");
                                return;
                        }

                        userService.deleteUser(user, currentRole.getCompany());
                        confirmationDialog.close();
                        updateTable();
                });
                Button cancelButton = new Button("No", e -> confirmationDialog.close());
                HorizontalLayout buttonLayout = new HorizontalLayout(confirmationButton, cancelButton);

                confirmationLayout.add(confirmationText, buttonLayout);
                confirmationLayout.setAlignItems(Alignment.CENTER);
                confirmationDialog.add(confirmationLayout);

                btnDelete.addClickListener(e -> {
                        if (!user.equals(currentUser)) {
                                confirmationDialog.open();
                        } else {
                                NotificationService.showNotification(NotificationVariant.LUMO_ERROR,
                                                "You Can't Delete Yourself");
                        }
                });

                return btnDelete;
        }

        @Override
        public void beforeEnter(BeforeEnterEvent event) {
                if (this.session.getAttribute("userID") != null) {
                        if (!currentUser.isActivated()) {
                                event.forwardTo(UserActivationForm.class);
                        }
                }
        }
}
