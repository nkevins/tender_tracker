package com.chlorocode.tendertracker.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Kyaw Min Thu on 6/1/2018.
 * Milestone entity.
 */
@Entity
@Table(name = "milestone")
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(DataTablesOutput.View.class)
    private int id;

    @ManyToOne
    private Tender tender;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "due_date")
    private Date dueDate;

    private int status;

    @Column(name = "notify_status")
    private int notifyStatus;

    @Column(name = "created_by")
    private int createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_updated_by")
    private int lastUpdatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated_date")
    private Date lastUpdatedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tender getTender() {
        return tender;
    }

    public void setTender(Tender tender) {
        this.tender = tender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(int notifyStatus) {
        this.notifyStatus = notifyStatus;
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

    @Override
    public String toString() {
        return "Milestone{" +
                "id=" + id +
                ", tender=" + tender +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", notifyStatus=" + notifyStatus +
                ", createdBy=" + createdBy +
                ", createdDate=" + createdDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdatedDate=" + lastUpdatedDate +
                '}';
    }
}
