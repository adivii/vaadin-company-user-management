package com.adivii.companymanagement.data.service.filter;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.service.CompanyService;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;

public class CompanyFilterService {
    private TreeDataProvider<Company> dataProvider;
    private CompanyService companyService;

    private String companyName;
    private String address;
    private String sector;
    private String website;
    private int employee;

    private String searchTerm;

    public void setDataProvider(TreeDataProvider<Company> dataProvider) {
        this.dataProvider = dataProvider;
        this.dataProvider.addFilter(this::filter);
        this.dataProvider.addFilter(this::search);
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
        this.dataProvider.refreshAll();
    }

    public void setAddress(String address) {
        this.address = address;
        this.dataProvider.refreshAll();
    }

    public void setSector(String sector) {
        this.sector = sector;
        this.dataProvider.refreshAll();
    }

    public void setWebsite(String website) {
        this.website = website;
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

    // Custom Function
    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isBlank() || value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    // TODO: Set Filter for User Count
    private boolean filter(Company company) {
        return matches(company.getCompanyName(), this.companyName) &&
                matches(company.getAddress(), this.address) &&
                matches(company.getSector(), this.sector) &&
                matches(company.getWebsite(), this.website);
    }

    private boolean search(Company company) {
        return matches(company.getCompanyName(), this.searchTerm) ||
                matches(company.getAddress(), this.searchTerm) ||
                matches(company.getSector(), this.searchTerm) ||
                matches(company.getWebsite(), this.searchTerm);
    }
}
