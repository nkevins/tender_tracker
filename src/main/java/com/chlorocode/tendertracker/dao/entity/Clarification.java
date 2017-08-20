package com.chlorocode.tendertracker.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by andy on 8/8/2017.
 */
@Entity
@Table(name = "clarification")
public class Clarification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(DataTablesOutput.View.class)
    private int id;

    @JsonView(DataTablesOutput.View.class)
    @ManyToOne
    private Company company;

    @JsonView(DataTablesOutput.View.class)
    @ManyToOne
    private Tender tender;

    @JsonView(DataTablesOutput.View.class)
    private String description;

    private int status;

    @JsonView(DataTablesOutput.View.class)
    private String response;

    @Column(name = "created_by")
    private int createdBy;

    @JsonView(DataTablesOutput.View.class)
    @Temporal(TemporalType.DATE)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_updated_by")
    private int lastUpdatedBy;

    @JsonView(DataTablesOutput.View.class)
    @Temporal(TemporalType.DATE)
    @Column(name = "last_updated_date")
    private Date lastUpdatedDate;

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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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
}
