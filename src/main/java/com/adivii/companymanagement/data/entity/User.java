package com.adivii.companymanagement.data.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    @NotNull
    private String email;
    private String firstName = "null";
    private String lastName = "null";
    private String address = "null";
    private String phoneNumber = "0";
    private boolean enabled;
    private boolean activated;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<RoleMap> roleId;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "avatar")
    private Avatar avatar;

    @OneToOne(cascade = CascadeType.MERGE)
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
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public List<RoleMap> getRoleId() {
        return roleId;
    }

    public void setRoleId(List<RoleMap> roleId) {
        this.roleId = roleId;
    }

    // Custom Function
    public boolean checkEmpty() {
        return email.isBlank();
    }

    public boolean checkIncompleted() {
        return (firstName.isBlank() || lastName.isBlank() || address.isBlank() || phoneNumber.isBlank());
    }

    @Override
    public boolean equals(Object arg0) {
        if(arg0 instanceof User) {
            return ((User) arg0).getUserId().equals(this.userId);
        }

        return false;
    }
}
