package com.chlorocode.tendertracker.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(DataTablesOutput.View.class)
    private int id;

    private double totalAmount;
    private int createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private int lastUpdatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedDate;

    @ManyToOne
    @JsonView(DataTablesOutput.View.class)
    private Tender tender;

    @ManyToOne
    private Company company;

    @OneToMany(mappedBy = "bid", cascade = CascadeType.PERSIST)
    private List<BidItem> bidItems;

    @OneToMany(mappedBy = "bid", cascade = CascadeType.PERSIST)
    private List<BidDocument> documents;

    @OneToMany(mappedBy = "bid", cascade = CascadeType.PERSIST)
    private List<EvaluationResult> evaluationResults;

    public Bid() {
        this.bidItems = new LinkedList<>();
        this.documents = new LinkedList<>();
        this.totalAmount = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotalAmount() {
        return totalAmount;
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

    public Tender getTender() {
        return tender;
    }

    public void setTender(Tender tender) {
        this.tender = tender;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<BidItem> getBidItems() {
        return bidItems;
    }

    public void addBidItem(BidItem bidItem) {
        bidItem.setBid(this);
        this.bidItems.add(bidItem);
        this.totalAmount += bidItem.getAmount();
    }

    public List<BidDocument> getDocuments() {
        return documents;
    }

    public void addBidDocument(BidDocument bidDocument) {
        bidDocument.setBid(this);
        this.documents.add(bidDocument);
    }
}
