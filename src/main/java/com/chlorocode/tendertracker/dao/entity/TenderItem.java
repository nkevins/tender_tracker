package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tender_item")
public class TenderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int uom;
    private float quantity;
    private String description;
    private int createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private int lastUpdatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedDate;

    @ManyToOne
    private Tender tender;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUom() {
        return uom;
    }

    public void setUom(int uom) {
        this.uom = uom;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
