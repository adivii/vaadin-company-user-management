package com.adivii.companymanagement.data.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.vaadin.flow.component.polymertemplate.Id;

@Entity
public class Account {
    @Id
    @NotNull
    Integer id;
    @NotNull
    private String emailAddress;
    private String password;

    @OneToOne(mappedBy = "account")
    private User user;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
