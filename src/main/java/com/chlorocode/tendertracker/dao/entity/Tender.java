package com.chlorocode.tendertracker.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Tender {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(DataTablesOutput.View.class)
    private int id;

    @JsonView(DataTablesOutput.View.class)
    private String refNo;

    @JsonView(DataTablesOutput.View.class)
    private String title;

    @Temporal(TemporalType.DATE)
    @JsonView(DataTablesOutput.View.class)
    private Date openDate;

    @Temporal(TemporalType.DATE)
    @JsonView(DataTablesOutput.View.class)
    private Date closedDate;

    @ManyToOne
    private Company company;

    @ManyToOne
    private TenderCategory tenderCategory;

    private String description;
    private int tenderType;
    private float estimatePurchaseValue;
    private float actualPurchaseValue;
    private Date deliveryDate;
    private String deliveryLocation;
    private String deliveryRemarks;
    private String contactPersonName;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private int status;
    private int createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private int lastUpdatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedDate;

    @OneToMany(mappedBy = "tender", cascade = CascadeType.PERSIST)
    private List<TenderItem> items;

    @OneToMany(mappedBy = "tender", cascade = CascadeType.PERSIST)
    private List<TenderDocument> documents;

    @OneToMany(mappedBy = "tender", cascade = CascadeType.PERSIST)
    private List<Bid> bids;

    @OneToMany(mappedBy = "tender", cascade = CascadeType.PERSIST)
    private List<TenderBookmark> tenderBookmarks;

    public Tender() {
        items = new LinkedList<>();
        documents = new LinkedList<>();
        tenderBookmarks = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public TenderCategory getTenderCategory() {
        return tenderCategory;
    }

    public void setTenderCategory(TenderCategory tenderCategory) {
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

    public float getActualPurchaseValue() {
        return actualPurchaseValue;
    }

    public void setActualPurchaseValue(float actualPurchaseValue) {
        this.actualPurchaseValue = actualPurchaseValue;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public List<TenderItem> getItems() {
        return items;
    }

    public void addTenderItem(TenderItem item) {
        items.add(item);
        item.setTender(this);
    }

    public List<TenderDocument> getDocuments() {
        return documents;
    }

    public void addTenderDocument(TenderDocument document) {
        documents.add(document);
        document.setTender(this);
    }

    public List<Bid> getBids() {
        return bids;
    }

    public List<TenderBookmark> getTenderBookmarks() {
        return tenderBookmarks;
    }
}
