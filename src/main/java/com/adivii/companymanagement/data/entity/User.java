package com.adivii.companymanagement.data.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    @NotNull
    private String email;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String address;
    @NotNull
    private String phoneNumber;
    @NotNull
    private boolean enabled;
    @NotNull
    private boolean activated;

    @ManyToOne
    @JoinColumn(name = "departmentId")
    @NotNull
    private Department departmentId;
    
    @ManyToOne
    @JoinColumn(name = "role")
    @NotNull
    private Role role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar")
    private Avatar avatar;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account")
    private Account account;
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }
    public Avatar getAvatar() {
        return avatar;
    }
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public Department getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(Department departmentId) {
        this.departmentId = departmentId;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public boolean isActivated() {
        return activated;
    }
    public void setActivated(boolean activated) {
        this.activated = activated;
    }
    
    // Custom Function
    public boolean checkEmpty() {
        return firstName.isBlank() || lastName.isBlank() || address.isBlank() || email.isBlank() || phoneNumber.isBlank();
    }
}
