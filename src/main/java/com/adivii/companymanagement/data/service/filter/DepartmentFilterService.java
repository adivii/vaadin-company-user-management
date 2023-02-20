package com.adivii.companymanagement.data.service.filter;

import com.adivii.companymanagement.data.entity.Department;
import com.vaadin.flow.data.provider.ListDataProvider;

public class DepartmentFilterService {
    private String departmentName;
    private String companyName;
    private int employee;

    private String searchTerm;

    private ListDataProvider<Department> dataProvider;

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
        this.dataProvider.refreshAll();
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
        this.dataProvider.refreshAll();
    }

    public void setEmployee(int employee) {
        this.employee = employee;
        this.dataProvider.refreshAll();
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        this.dataProvider.refreshAll();
    }

    public void setDataProvider(ListDataProvider<Department> dataProvider) {
        this.dataProvider = dataProvider;
        this.dataProvider.addFilter(this::filter);
        this.dataProvider.addFilter(this::search);
    }

    // Custom Function

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isBlank() || value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    // TODO: Set Filter for User Count
    private boolean filter(Department department) {
        return matches(department.getName(), this.departmentName) &&
                matches(department.getCompanyId().getCompanyName(), this.companyName);
    }

    private boolean search(Department department) {
        return matches(department.getName(), this.searchTerm) ||
                matches(department.getCompanyId().getCompanyName(), this.searchTerm);
    }
}
