package com.chlorocode.tendertracker.dao.dto;

import com.chlorocode.tendertracker.dao.entity.TenderDocument;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TenderUpdateDTO {
    private int tenderId;
    private String refNo;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Tender Open Date is required")
    private Date openDate;

    @NotNull(message = "Tender Closed Date is requried")
    private Date closedDate;

    @NotNull(message = "Tender Category is required")
    private int tenderCategory;
    private String description;

    @NotNull(message = "Tender Type is required")
    private int tenderType;

    @NotNull(message = "EPV is required")
    private float estimatePurchaseValue;
    private Date deliveryDate;
    private String deliveryLocation;
    private String deliveryRemarks;

    @NotBlank(message = "Contact Person Name is required")
    private String contactPersonName;

    @NotBlank(message = "Contact Person Email is required")
    private String contactPersonEmail;

    @NotBlank(message = "Contact Person Phone is required")
    private String contactPersonPhone;

    @Valid
    private List<TenderItemUpdateDTO> items;

    private List<TenderDocument> documents;

    public TenderUpdateDTO() {
        items = new LinkedList<>();
    }

    public int getTenderId() {
        return tenderId;
    }

    public void setTenderId(int tenderId) {
        this.tenderId = tenderId;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
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

    public int getTenderCategory() {
        return tenderCategory;
    }

    public void setTenderCategory(int tenderCategory) {
        this.tenderCategory = tenderCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTenderType() {
        return tenderType;
    }

    public void setTenderType(int tenderType) {
        this.tenderType = tenderType;
    }

    public float getEstimatePurchaseValue() {
        return estimatePurchaseValue;
    }

    public void setEstimatePurchaseValue(float estimatePurchaseValue) {
        this.estimatePurchaseValue = estimatePurchaseValue;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public String getDeliveryRemarks() {
        return deliveryRemarks;
    }

    public void setDeliveryRemarks(String deliveryRemarks) {
        this.deliveryRemarks = deliveryRemarks;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public List<TenderItemUpdateDTO> getItems() {
        return items;
    }

    public void addTenderItem(TenderItemUpdateDTO item) {
        items.add(item);
    }

    public List<TenderDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<TenderDocument> documents) {
        this.documents = documents;
    }
}
