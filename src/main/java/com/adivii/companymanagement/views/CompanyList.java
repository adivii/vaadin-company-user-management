package com.adivii.companymanagement.views;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.servlet.http.HttpSession;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.filter.CompanyFilterService;
import com.adivii.companymanagement.views.component.CustomAvatar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

// Add alternative Route for this page
// So, both localhost:8080 and localhost:8080/company will open this page
// @PWA(name = "Company Management App",
//      shortName = "cm-app")
@Route("/company")
@PageTitle("Company List")
public class CompanyList extends HorizontalLayout implements BeforeEnterObserver {
    User currentUser;

    CompanyService companyService;
    UserService userService;
    CompanyFilterService companyFilterService;

    TreeGrid<Company> companyTable;
    HttpSession session;

    public CompanyList(CompanyService companyService, UserService userService) {
        this.companyService = companyService;
        this.userService = userService;
        this.companyFilterService = new CompanyFilterService();
        session = SessionService.getCurrentSession();

        if (this.session.getAttribute("userID") != null) {
            currentUser = userService.getUser((Integer) this.session.getAttribute("userID")).get();
        }

        VerticalLayout sidebar = new SidebarLayout(this.userService);
        VerticalLayout mainLayout = getLayout();

        sidebar.setWidth("250px");

        setSizeFull();
        add(sidebar, mainLayout);
    }

    public Grid<Company> getCompanyTable() {
        this.companyTable = new TreeGrid<>();

        this.companyTable.addComponentColumn(e -> {
            Button button = getDeleteButton(e);

            return button;
        }).setWidth("75px").setFlexGrow(0).setFrozen(true);
        this.companyTable.addComponentColumn(e -> {
            CustomAvatar avatar = new CustomAvatar(e.getCompanyName());
            avatar.setColor(((int) e.getCompanyName().charAt(0)) % 4);

            if (e.getAvatar() != null) {
                avatar.setAvatar(new Image(e.getAvatar().getUri(), null));
            }

            return avatar;
        }).setWidth("70px").setFlexGrow(0).setFrozen(true);
        Grid.Column<Company> nameColumn = this.companyTable.addComponentHierarchyColumn(e -> {
            Label text = new Label(e.getCompanyName());
            text.setTitle(e.getCompanyName());

            return text;
        }).setAutoWidth(true).setResizable(true).setFrozen(true);
        Grid.Column<Company> addressColumn = this.companyTable.addColumn(TemplateRenderer.<Company>of(
                "<span title='[[item.address]]' aria-label='[[item.address]]'>[[item.address]]</span>")
                .withProperty("address", Company::getAddress)).setAutoWidth(true).setResizable(true);
        Grid.Column<Company> sectorColumn = this.companyTable.addColumn(TemplateRenderer.<Company>of(
                "<span title='[[item.sector]]' aria-label='[[item.sector]]'>[[item.sector]]</span>")
                .withProperty("sector", Company::getSector)).setAutoWidth(true).setResizable(true);
        Grid.Column<Company> employeeColumn = this.companyTable.addComponentColumn(e -> {
            Label count = new Label();

            count.setText(Integer.toString(this.userService.getByCompany(e).size()));
            count.setTitle(Integer.toString(this.userService.getByCompany(e).size()));

            return count;
        }).setAutoWidth(true).setResizable(true);
        Grid.Column<Company> websiteColumn = this.companyTable.addComponentColumn(e -> {
            Anchor link = new Anchor(e.getWebsite(), e.getWebsite());
            link.setTitle(e.getWebsite());

            return link;
        }).setAutoWidth(true).setResizable(true);

        this.companyTable.addItemDoubleClickListener(e -> {
            getEditCompanyDialog(e.getItem()).open();
        });

        updateTable();
        this.companyTable.setHeightFull();

        this.companyTable.getHeaderRows().clear();
        HeaderRow header = this.companyTable.appendHeaderRow();

        header.getCell(nameColumn).setComponent(
                createFilterHeader("Company Name", this.companyFilterService::setCompanyName));
        header.getCell(addressColumn).setComponent(
                createFilterHeader("Address", this.companyFilterService::setAddress));
        header.getCell(sectorColumn).setComponent(
                createFilterHeader("Sector", this.companyFilterService::setSector));
        header.getCell(employeeColumn).setComponent(
                createFilterHeader("No of Employee", e -> this.companyFilterService.setEmployee(Integer.parseInt(e))));
        header.getCell(websiteColumn).setComponent(
                createFilterHeader("Website", this.companyFilterService::setWebsite));

        return this.companyTable;
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
        TreeData<Company> data = new TreeData<>();
        List<Company> holdings = companyService.getByName(currentUser.getRoleId().getCompany().getCompanyName());

        data.addItems(null, holdings);
        holdings.forEach(holding -> data.addItems(holding, companyService.getChildCompany(holding)));

        TreeDataProvider<Company> provider = new TreeDataProvider<>(data);

        // userTable.setItems(items);
        companyTable.setDataProvider(provider);
        companyFilterService.setDataProvider(provider);
    }

    public List<Company> getChild(Company holdingCompany) {
        return companyService.getChildCompany(holdingCompany);
    }

    public VerticalLayout getLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        Button btnAdd = new Button("Add New Company", e -> {
            getAddCompanyDialog().open();
            System.out.println(session.getAttribute("userID"));
        });
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search");
        HorizontalLayout searchLayout = new HorizontalLayout(btnAdd, searchField);
        Grid<Company> companyTable = getCompanyTable();

        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            this.companyFilterService.setSearchTerm(e.getValue());

            searchField.setClearButtonVisible(e.getValue() != null);
        });

        mainLayout.add(searchLayout, companyTable);
        mainLayout.setHeightFull();

        return mainLayout;
    }

    public Dialog getAddCompanyDialog() {
        Dialog addCompanyDialog = new Dialog();
        addCompanyDialog.setModal(true);
        addCompanyDialog.setCloseOnEsc(false);
        addCompanyDialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        H3 title = new H3("Add New Company");
        Hr divider = new Hr();

        divider.setWidthFull();
        divider.setHeight("3px");

        // Create Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> addCompanyDialog.close());

        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(btnSave, btnCancel);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();

        // Text Field
        TextField nameInput = new TextField("Company Name");
        TextArea addressInput = new TextArea("Company Address");
        TextField sectorInput = new TextField("Company Sector");
        TextField websiteInput = new TextField("Company Website");
        ComboBox<Company> holdingInput = new ComboBox<>("Holding Company");

        nameInput.setWidthFull();
        sectorInput.setWidthFull();
        websiteInput.setWidthFull();
        addressInput.setWidthFull();
        addressInput.setMaxLength(255);
        addressInput.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/255");
        });
        addressInput.setHelperText("0/255");
        addressInput.setValueChangeMode(ValueChangeMode.EAGER);
        holdingInput.setWidthFull();
        List<Company> companyItems = new ArrayList<>();
        if (this.currentUser.getRoleId().getCompany().getHoldingCompany() == null) {
            companyItems
                    .addAll(this.companyService.getByName(this.currentUser.getRoleId().getCompany().getCompanyName()));
        } else {
            this.currentUser.getRoleId().getCompany().getHoldingCompany();
        }
        holdingInput.setItems(companyItems);
        holdingInput.setItemLabelGenerator(Company::getCompanyName);
        holdingInput.addValueChangeListener(e -> {
            holdingInput.setClearButtonVisible(holdingInput.getValue() != null);
        });

        Scroller scroller = new Scroller(new Div(nameInput, addressInput, sectorInput, websiteInput, holdingInput));
        scroller.setHeightFull();
        scroller.setWidthFull();
        scroller.getStyle()
                .set("padding", "var(--lumo-space-s)");

        // Save Data
        btnSave.addClickListener(e -> {
            Company newCompany = new Company();
            newCompany.setCompanyName(nameInput.getValue());
            newCompany.setAddress(addressInput.getValue());
            newCompany.setSector(sectorInput.getValue());
            newCompany.setWebsite(websiteInput.getValue());
            newCompany.setHoldingCompany(holdingInput.getValue());

            ErrorService errorService = companyService.addCompany(newCompany);
            if (!errorService.isErrorStatus()) {
                addCompanyDialog.close();
                updateTable();
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

        dialogLayout.setWidth("500px");
        dialogLayout.setHeight("500px");
        dialogLayout.add(title, divider, scroller, buttonLayout);
        addCompanyDialog.add(dialogLayout);

        return addCompanyDialog;
    }

    public Dialog getEditCompanyDialog(Company data) {
        Dialog editCompanyDialog = new Dialog();
        editCompanyDialog.setModal(true);
        editCompanyDialog.setCloseOnEsc(false);
        editCompanyDialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        H3 title = new H3("Edit Company");
        Hr divider = new Hr();

        divider.setWidthFull();
        divider.setHeight("3px");

        // Create Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> editCompanyDialog.close());

        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(btnSave, btnCancel);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();

        // Text Field
        TextField nameInput = new TextField("Company Name");
        TextArea addressInput = new TextArea("Company Address");
        TextField sectorInput = new TextField("Company Sector");
        TextField websiteInput = new TextField("Company Website");
        ComboBox<Company> holdingInput = new ComboBox<>("Holding Company");

        nameInput.setWidthFull();
        sectorInput.setWidthFull();
        websiteInput.setWidthFull();
        addressInput.setWidthFull();
        addressInput.setMaxLength(255);
        addressInput.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/255");
        });
        addressInput.setHelperText("0/255");
        addressInput.setValueChangeMode(ValueChangeMode.EAGER);
        holdingInput.setWidthFull();
        List<Company> companyItems = new ArrayList<>();
        if (this.currentUser.getRoleId().getCompany().getHoldingCompany() == null) {
            companyItems
                    .addAll(this.companyService.getByName(this.currentUser.getRoleId().getCompany().getCompanyName()));
        } else {
            this.currentUser.getRoleId().getCompany().getHoldingCompany();
        }
        holdingInput.setItems(companyItems);
        holdingInput.setItemLabelGenerator(Company::getCompanyName);

        Scroller scroller = new Scroller(new Div(nameInput, addressInput, sectorInput, websiteInput, holdingInput));
        scroller.setHeightFull();
        scroller.setWidthFull();
        scroller.getStyle()
                .set("padding", "var(--lumo-space-s)");

        // Set Content
        nameInput.setValue(data.getCompanyName());
        addressInput.setValue(data.getAddress());
        sectorInput.setValue(data.getSector());
        websiteInput.setValue(data.getWebsite());
        holdingInput.setValue(data.getHoldingCompany());

        // Save Data
        btnSave.addClickListener(e -> {
            Company currentCompany = data;
            currentCompany.setCompanyName(nameInput.getValue());
            currentCompany.setAddress(addressInput.getValue());
            currentCompany.setSector(sectorInput.getValue());
            currentCompany.setWebsite(websiteInput.getValue());
            currentCompany.setHoldingCompany(holdingInput.getValue());

            ErrorService errorService = companyService.editData(currentCompany);
            if (!errorService.isErrorStatus()) {
                editCompanyDialog.close();
                updateTable();
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

        dialogLayout.setWidth("500px");
        dialogLayout.setHeight("500px");
        dialogLayout.add(title, divider, scroller, buttonLayout);
        editCompanyDialog.add(dialogLayout);

        return editCompanyDialog;
    }

    public Button getDeleteButton(Company company) {
        Button btnDelete = new Button(new Icon(VaadinIcon.TRASH));
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);

        // Create Confirmation Dialog
        Dialog confirmationDialog = new Dialog();
        VerticalLayout confirmationLayout = new VerticalLayout();

        Text confirmationText = new Text("Are you sure?");
        Button confirmationButton = new Button("Yes", e -> {
            if (companyService.deleteCompany(company)) {
                confirmationDialog.close();
                updateTable();
            } else {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                Text notificationText = new Text("Can't Delete Record");
                Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), i -> notification.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                HorizontalLayout notificationLayout = new HorizontalLayout(notificationText, closeButton);

                notification.setDuration(2000);
                notification.add(notificationLayout);
                notification.open();

                confirmationDialog.close();
            }
        });
        Button cancelButton = new Button("No", e -> confirmationDialog.close());
        HorizontalLayout buttonLayout = new HorizontalLayout(confirmationButton, cancelButton);

        confirmationLayout.add(confirmationText, buttonLayout);
        confirmationLayout.setAlignItems(Alignment.CENTER);
        confirmationDialog.add(confirmationLayout);

        btnDelete.addClickListener(e -> {
            confirmationDialog.open();
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
