package com.chlorocode.tendertracker.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(DataTablesOutput.View.class)
    private int id;

    @JsonView(DataTablesOutput.View.class)
    private String name;
    @JsonView(DataTablesOutput.View.class)
    private String email;
    private String contactNo;
    private String password;
    private int status;
    private String confirmationToken;
//    private Date tokenExpireDate; //TODO need to add after field added in db.

    @OneToMany
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private List<UserRole> userRoles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<TenderBookmark> tenderBookmarks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<TenderCategorySubscription> tenderCategorySubscriptions;

    private int createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private int lastUpdatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedDate;


    public User() {
        this.tenderBookmarks = new LinkedList<>();
    }

    public User(String name, String email, String contactNo, String password) {
        this.name = name;
        this.email = email;
        this.contactNo = contactNo;
        this.password = password;
        this.tenderBookmarks = new LinkedList<>();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(int lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public List<TenderBookmark> getTenderBookmarks() {
        return tenderBookmarks;
    }

    public List<TenderCategorySubscription> getTenderCategorySubscriptions() {
        return tenderCategorySubscriptions;
    }

    //    public Date getTokenExpireDate() {
//        return tokenExpireDate;
//    }
//
//    public void setTokenExpireDate(Date tokenExpireDate) {
//        this.tokenExpireDate = tokenExpireDate;
//    }
}
