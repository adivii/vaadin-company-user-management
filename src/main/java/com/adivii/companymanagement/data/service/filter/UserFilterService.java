package com.adivii.companymanagement.data.service.filter;

import com.adivii.companymanagement.data.entity.User;
import com.vaadin.flow.data.provider.ListDataProvider;

public class UserFilterService {
    private ListDataProvider<User> dataProvider;

    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private String company;
    private String department;

    private String searchTerm;

    public void setDataProvider(ListDataProvider<User> dataProvider) {
        this.dataProvider = dataProvider;
        this.dataProvider.addFilter(this::filter);
        this.dataProvider.addFilter(this::search);
    }

    public void setEmail(String email) {
        this.email = email;
        this.dataProvider.refreshAll();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.dataProvider.refreshAll();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.dataProvider.refreshAll();
    }

    public void setAddress(String address) {
        this.address = address;
        this.dataProvider.refreshAll();
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.dataProvider.refreshAll();
    }

    public void setCompany(String company) {
        this.company = company;
        this.dataProvider.refreshAll();
    }

    public void setDepartment(String department) {
        this.department = department;
        this.dataProvider.refreshAll();
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        this.dataProvider.refreshAll();
    }

    // Custom Function
    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isBlank() || value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private boolean filter(User user) {
        return matches(user.getEmailAddress(), email) &&
                matches(user.getFirstName(), firstName) &&
                matches(user.getLastName(), lastName) &&
                matches(user.getAddress(), address) &&
                matches(user.getPhoneNumber(), phone) &&
                matches(user.getDepartmentId().getCompanyId().getCompanyName(), company) &&
                matches(user.getDepartmentId().getName(), department);
    }

    private boolean search(User user) {
        return matches(user.getEmailAddress(), searchTerm) ||
                matches(user.getFirstName(), searchTerm) ||
                matches(user.getLastName(), searchTerm) ||
                matches(user.getAddress(), searchTerm) ||
                matches(user.getPhoneNumber(), searchTerm) ||
                matches(user.getDepartmentId().getCompanyId().getCompanyName(), searchTerm) ||
                matches(user.getDepartmentId().getName(), searchTerm);
    }
}
