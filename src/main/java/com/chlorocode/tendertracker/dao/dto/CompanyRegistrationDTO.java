package com.chlorocode.tendertracker.dao.dto;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class CompanyRegistrationDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "GST Registration No is required")
    private String gstRegNo;

    @NotBlank(message = "UEN is required")
    private String uen;

    @NotNull(message = "Company Type is required")
    private int type;

    @NotNull(message = "Area of Business is required")
    private int areaOfBusiness;

    @NotBlank(message = "Address 1 is required")
    private String address1;

    private String address2;

    @NotBlank(message = "Postal Code is required")
    private String postalCode;

    private String city;

    private String state;

    private String province;

    @NotBlank(message = "Country is required")
    private String country;

    private String principleBusinessActivity;

    public String getPrincipleBusinessActivity() {
        return principleBusinessActivity;
    }

    public void setPrincipleBusinessActivity(String principleBusinessActivity) {
        this.principleBusinessActivity = principleBusinessActivity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGstRegNo() {
        return gstRegNo;
    }

    public void setGstRegNo(String gstRegNo) {
        this.gstRegNo = gstRegNo;
    }

    public String getUen() {
        return uen;
    }

    public void setUen(String uen) {
        this.uen = uen;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAreaOfBusiness() {
        return areaOfBusiness;
    }

    public void setAreaOfBusiness(int areaOfBusiness) {
        this.areaOfBusiness = areaOfBusiness;
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
}
