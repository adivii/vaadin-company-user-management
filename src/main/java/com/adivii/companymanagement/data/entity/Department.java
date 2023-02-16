package com.adivii.companymanagement.data.entity;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Formula;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer departmentId;
    
    @NotNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "companyId")
    @NotNull
    private Company companyId;
    @OneToMany(mappedBy = "departmentId")
    private List<User> user = new LinkedList<>();

    @Formula("(SELECT COUNT(user.user_id) FROM user WHERE user.department_id = department_id)")
    private int userCount;

    public List<User> getUser() {
        return user;
    }
    public void setUser(List<User> user) {
        this.user = user;
    }
    public int getUserCount() {
        return userCount;
    }
    public Integer getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Company getCompanyId() {
        return companyId;
    }
    public void setCompanyId(Company companyId) {
        this.companyId = companyId;
    }

    // Custom Function
    public boolean checkEmpty() {
        return name.isBlank() || companyId == null;
    }
}
