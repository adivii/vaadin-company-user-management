package com.adivii.companymanagement.data.entity;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Formula;

@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Enable Auto Increment
    private Integer companyId;

    @NotNull
    private String companyName;
    private String address;
    private String sector;
    private String website;

    @OneToMany(mappedBy = "companyId")
    private List<Department> department = new LinkedList<>();

    @OneToMany(mappedBy = "holdingCompany")
    private List<Company> childCompany = new LinkedList<>();

    @OneToMany(mappedBy = "company")
    private List<RoleMap> user = new LinkedList<>();

    @ManyToOne
    @JoinColumn(name = "holdingCompany")
    private Company holdingCompany;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "avatar")
    private Avatar avatar;

    // TODO: Implement count in service
    // @Formula("(SELECT COUNT(user.user_id) FROM user INNER JOIN department ON user.department_id = department.department_id WHERE department.company_id = company_id)")
    // private int userCount;
    @Formula("(SELECT COUNT(department.department_id) FROM department WHERE department.company_id = company_id)")
    private int departmentCount;
    @Formula("(SELECT COUNT(company.company_id) FROM company WHERE company.holding_company = company_id)")
    private int childCompanyCount;

    public int getChildCompanyCount() {
        return childCompanyCount;
    }
    public int getDepartmentCount() {
        return departmentCount;
    }
    // public Integer getUserCount() {
    //     return userCount;
    // }
    public List<Department> getDepartment() {
        return department;
    }
    public void setDepartment(List<Department> department) {
        this.department = department;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getSector() {
        return sector;
    }
    public void setSector(String sector) {
        this.sector = sector;
    }
    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public Integer getCompanyId() {
        return companyId;
    }
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
    public List<Company> getChildCompany() {
        return childCompany;
    }
    public void setChildCompany(List<Company> childCompany) {
        this.childCompany = childCompany;
    }
    public Company getHoldingCompany() {
        return holdingCompany;
    }
    public void setHoldingCompany(Company holdingCompany) {
        this.holdingCompany = holdingCompany;
    }
    public Avatar getAvatar() {
        return avatar;
    }
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }
    public List<RoleMap> getUser() {
        return user;
    }
    public void setUser(List<RoleMap> user) {
        this.user = user;
    }

    // Custom Function
    public boolean checkEmpty() {
        return companyName.isBlank() || address.isBlank() || sector.isBlank() || website.isBlank();
    }
}
