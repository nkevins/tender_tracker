package com.chlorocode.tendertracker.dao.dto;

import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;

/**
 * Created by andy on 8/8/2017.
 */
public class TenderClarificationDTO {
    private int id;

    @NotBlank(message = "Please enter your response")
    private String response;
    private String title;
    private Date openDate;
    private Date closedDate;
    private String refNo;
    private String category;
    private String clarification;
    private Date submittedDate;
    private String submittedByCompany;
    private String submittedByName;
    private String submittedByEmail;
    private String submittedByContactNo;
    private String tenderType;

    public String getTenderType() {
        return tenderType;
    }

    public void setTenderType(String tenderType) {
        this.tenderType = tenderType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getClarification() {
        return clarification;
    }

    public void setClarification(String clarification) {
        this.clarification = clarification;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getSubmittedByCompany() {
        return submittedByCompany;
    }

    public void setSubmittedByCompany(String submittedByCompany) {
        this.submittedByCompany = submittedByCompany;
    }

    public String getSubmittedByName() {
        return submittedByName;
    }

    public void setSubmittedByName(String submittedByName) {
        this.submittedByName = submittedByName;
    }

    public String getSubmittedByEmail() {
        return submittedByEmail;
    }

    public void setSubmittedByEmail(String submittedByEmail) {
        this.submittedByEmail = submittedByEmail;
    }

    public String getSubmittedByContactNo() {
        return submittedByContactNo;
    }

    public void setSubmittedByContactNo(String submittedByContactNo) {
        this.submittedByContactNo = submittedByContactNo;
    }
}
