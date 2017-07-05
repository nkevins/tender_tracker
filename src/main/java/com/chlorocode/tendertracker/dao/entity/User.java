package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private String email;
    private String contactNo;
    private String password;
    private int status;
    private String confirmationToken;

    @OneToMany(mappedBy = "applicant")
    private Collection<CompanyRegistration> companyRegistrations;

    @OneToMany
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private List<UserRole> userRoles;

    private int createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private int lastUpdatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedDate;


    public User() { }

    public User(String name, String email, String contactNo, String password) {
        this.name = name;
        this.email = email;
        this.contactNo = contactNo;
        this.password = password;
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

    public Collection<CompanyRegistration> getCompanyRegistrations() {
        return companyRegistrations;
    }

    public void setCompanyRegistrations(Collection<CompanyRegistration> companyRegistrations) {
        this.companyRegistrations = companyRegistrations;
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
}
