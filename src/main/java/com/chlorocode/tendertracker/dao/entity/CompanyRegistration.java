package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "company_registration")
public class CompanyRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private String uen;
    private String gstRegNo;
    private int type;

    @Column(name = "address_line_1")
    private String address1;

    @Column(name = "address_line_2")
    private String address2;
    private String postalCode;
    private String city;
    private String state;
    private String province;
    private String country;
    private int areaOfBusiness;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User applicant;

    private int createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private int lastUpdatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedDate;

    public CompanyRegistration() {

    }

    public CompanyRegistration(String name, String uen, String gstRegNo, int type, String address1, String address2, String postalCode, String city, String state, String province, String country, int areaOfBusiness, User applicant, int createdBy, Date createdDate, int lastUpdatedBy, Date lastUpdatedDate) {
        this.name = name;
        this.uen = uen;
        this.gstRegNo = gstRegNo;
        this.type = type;
        this.address1 = address1;
        this.address2 = address2;
        this.postalCode = postalCode;
        this.city = city;
        this.state = state;
        this.province = province;
        this.country = country;
        this.areaOfBusiness = areaOfBusiness;
        this.applicant = applicant;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedDate = lastUpdatedDate;
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

    public String getUen() {
        return uen;
    }

    public void setUen(String uen) {
        this.uen = uen;
    }

    public String getGstRegNo() {
        return gstRegNo;
    }

    public void setGstRegNo(String gstRegNo) {
        this.gstRegNo = gstRegNo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAreaOfBusiness() {
        return areaOfBusiness;
    }

    public void setAreaOfBusiness(int areaOfBusiness) {
        this.areaOfBusiness = areaOfBusiness;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
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
