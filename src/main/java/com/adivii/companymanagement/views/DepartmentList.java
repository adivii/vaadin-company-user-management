package com.adivii.companymanagement.views;

import java.util.function.Consumer;

import javax.servlet.http.HttpSession;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.CompanyService;
import com.adivii.companymanagement.data.service.DepartmentService;
import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.SessionService;
import com.adivii.companymanagement.data.service.UserService;
import com.adivii.companymanagement.data.service.filter.DepartmentFilterService;
import com.adivii.companymanagement.views.component.CustomAvatar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
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

@Route("/department")
@PageTitle("Department List")
public class DepartmentList extends HorizontalLayout implements BeforeEnterObserver {
    private UserService userService;
    private DepartmentService departmentService;
    private CompanyService companyService;
    private Grid<Department> departmentTable;
    private HttpSession session;
    private DepartmentFilterService departmentFilterService;

    public DepartmentList(UserService userService, DepartmentService departmentService, CompanyService companyService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.companyService = companyService;
        this.departmentFilterService = new DepartmentFilterService();

        session = SessionService.getCurrentSession();

        if(this.session.getAttribute("userID") != null) {
            User user = userService.getUser((Integer) this.session.getAttribute("userID")).get();

            if(!user.isActivated()) {
                UI.getCurrent().getPage().setLocation("/setting");
            }
        }

        VerticalLayout sidebarLayout = new SidebarLayout(this.userService);

        sidebarLayout.setWidth("250px");

        setSizeFull();
        add(sidebarLayout, getLayout());
    }

    public Grid<Department> getDepartmentTable() {
        this.departmentTable = new Grid<>(Department.class, false);

        this.departmentTable.addComponentColumn(e -> {
            Button button = getDeleteButton(e);

            return button;
        }).setWidth("75px").setFlexGrow(0);
        this.departmentTable.addComponentColumn(e -> {
            CustomAvatar avatar = new CustomAvatar(e.getCompanyId().getCompanyName());
            avatar.setColor(((int) e.getCompanyId().getCompanyName().charAt(0)) % 4);

            if (e.getCompanyId().getAvatar() != null) {
                avatar.setAvatar(new Image(e.getCompanyId().getAvatar().getUri(), null));
            }

            return avatar;
        }).setWidth("70px").setFlexGrow(0).setFrozen(true);
        Grid.Column<Department> nameColumn = this.departmentTable.addColumn(TemplateRenderer.<Department>of(
                "<span title='[[item.name]]' aria-label='[[item.name]]'>[[item.name]]</span>")
                .withProperty("name", Department::getName)).setAutoWidth(true)
                .setResizable(true);
        Grid.Column<Department> companyColumn = this.departmentTable.addColumn(TemplateRenderer.<Department>of(
                "<span title='[[item.company]]' aria-label='[[item.company]]'>[[item.company]]</span>")
                .withProperty("company", e -> e.getCompanyId().getCompanyName()))
                .setAutoWidth(true).setResizable(true);
        Grid.Column<Department> employeeColumn = this.departmentTable.addColumn(TemplateRenderer.<Department>of(
                "<span title='[[item.employee]]' aria-label='[[item.employee]]'>[[item.employee]]</span>")
                .withProperty("employee", Department::getUserCount)).setAutoWidth(true)
                .setResizable(true);

        this.departmentTable.addItemDoubleClickListener(e -> {
            getEditDialog(e.getItem()).open();
        });

        this.departmentTable.setHeightFull();
        updateTable();

        this.departmentTable.getHeaderRows().clear();
        HeaderRow header = this.departmentTable.appendHeaderRow();

        header.getCell(nameColumn).setComponent(
                createFilterHeader("Department Name", this.departmentFilterService::setDepartmentName));
        header.getCell(companyColumn).setComponent(
                createFilterHeader("Department Name", this.departmentFilterService::setCompanyName));
        header.getCell(employeeColumn).setComponent(
                createFilterHeader("Department Name", this.departmentFilterService::setCompanyName));

        return this.departmentTable;
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
        ListDataProvider<Department> dataProvider = new ListDataProvider<>(departmentService.getAllDepartment());

        this.departmentTable.setDataProvider(dataProvider);
        this.departmentFilterService.setDataProvider(dataProvider);
    }

    public VerticalLayout getLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        Button btnAdd = new Button("Add New Department");
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search");
        HorizontalLayout searchLayout = new HorizontalLayout(btnAdd, searchField);

        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            this.departmentFilterService.setSearchTerm(e.getValue());
            searchField.setClearButtonVisible(e.getValue() != null);
        });

        btnAdd.addClickListener(e -> {
            getAddDepartmentDialog().open();
        });

        mainLayout.add(searchLayout, getDepartmentTable());
        mainLayout.setHeightFull();

        return mainLayout;
    }

    public Dialog getAddDepartmentDialog() {
        Dialog addDepartmentDialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        H3 title = new H3("Add New Department");
        Hr divider = new Hr();

        divider.setWidthFull();
        divider.setHeight("3px");

        // Input Field
        TextField nameInput = new TextField("Department Name");
        ComboBox<Company> companyInput = new ComboBox<>("Company Name");
        companyInput.setItems(companyService.getAllCompany());
        companyInput.setItemLabelGenerator(Company::getCompanyName);

        nameInput.setWidthFull();
        companyInput.setWidthFull();

        // Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> addDepartmentDialog.close());

        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(btnSave, btnCancel);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();

        // Save Mechanism
        btnSave.addClickListener(e -> {
            Department newDepartment = new Department();
            newDepartment.setName(nameInput.getValue());
            newDepartment.setCompanyId(companyInput.getValue());

            ErrorService errorService = departmentService.saveDepartment(newDepartment);
            if (!errorService.isErrorStatus()) {
                addDepartmentDialog.close();
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

        dialogLayout.add(title, divider, nameInput, companyInput, buttonLayout);
        addDepartmentDialog.add(dialogLayout);

        // Disable Close Without Button
        addDepartmentDialog.setModal(true);
        addDepartmentDialog.setCloseOnEsc(false);
        addDepartmentDialog.setCloseOnOutsideClick(false);

        return addDepartmentDialog;
    }

    public Dialog getEditDialog(Department department) {
        Dialog editDepartmentDialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        H3 title = new H3("Edit Department");
        Hr divider = new Hr();

        divider.setWidthFull();
        divider.setHeight("3px");

        // Input Field
        TextField nameInput = new TextField("Department Name");
        ComboBox<Company> companyInput = new ComboBox<>("Company Name");
        companyInput.setItems(companyService.getAllCompany());
        companyInput.setItemLabelGenerator(Company::getCompanyName);

        // Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel", e -> editDepartmentDialog.close());

        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(btnSave, btnCancel);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();

        // Set Value
        nameInput.setValue(department.getName());
        companyInput.setValue(department.getCompanyId());

        // Save Mechanism
        btnSave.addClickListener(e -> {
            Department newDepartment = department;
            newDepartment.setName(nameInput.getValue());
            newDepartment.setCompanyId(companyInput.getValue());

            ErrorService errorService = departmentService.editData(newDepartment);
            if (!errorService.isErrorStatus()) {
                editDepartmentDialog.close();
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

        dialogLayout.add(title, divider, nameInput, companyInput, buttonLayout);
        editDepartmentDialog.add(dialogLayout);

        // Disable Close Without Button
        editDepartmentDialog.setModal(true);
        editDepartmentDialog.setCloseOnEsc(false);
        editDepartmentDialog.setCloseOnOutsideClick(false);

        return editDepartmentDialog;
    }

    public Button getDeleteButton(Department department) {
        Button btnDelete = new Button(new Icon(VaadinIcon.TRASH));
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);

        // Create Confirmation Dialog
        Dialog confirmationDialog = new Dialog();
        VerticalLayout confirmationLayout = new VerticalLayout();

        Text confirmationText = new Text("Are you sure?");
        Button confirmationButton = new Button("Yes", e -> {
            if (departmentService.deleteDepartment(department)) {
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
        if(this.session.getAttribute("userID") != null) {
            User user = userService.getUser((Integer) this.session.getAttribute("userID")).get();

            if(!user.isActivated()) {
                event.forwardTo(UserSetting.class);;
            }
        } 
    }
}
