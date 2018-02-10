package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * TenderBookmark entity.
 */
@Entity
@Table(name = "tender_bookmark")
public class TenderBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Tender tender;

    private int createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private int lastUpdatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tender getTender() {
        return tender;
    }

    public void setTender(Tender tender) {
        this.tender = tender;
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
