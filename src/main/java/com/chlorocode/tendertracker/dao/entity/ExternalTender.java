package com.chlorocode.tendertracker.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;

/**
 * Created by Kyaw Min Thu on 3/7/2017.
 */
@Entity
@Table(name = "external_tender")
public class ExternalTender {
    @Id
    @JsonView(DataTablesOutput.View.class)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "reference_no")
    @JsonView(DataTablesOutput.View.class)
    private String referenceNo;

    @JsonView(DataTablesOutput.View.class)
    private String title;

    @JsonView(DataTablesOutput.View.class)
    @Column(name = "company_name")
    private String companyName;

    @JsonView(DataTablesOutput.View.class)
    @Column(name = "published_date")
    private String publishedDate;

    @JsonView(DataTablesOutput.View.class)
    @Column(name = "closing_date")
    private String closingDate;

    @JsonView(DataTablesOutput.View.class)
    private String status;

    @JsonView(DataTablesOutput.View.class)
    @Column(name = "tender_url")
    private String tenderURL;

    @JsonView(DataTablesOutput.View.class)
    @Column(name = "tender_source")
    private String tenderSource;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTenderURL() {
        return tenderURL;
    }

    public void setTenderURL(String tenderURL) {
        this.tenderURL = tenderURL;
    }

    public String getTenderSource() {
        return tenderSource;
    }

    public void setTenderSource(String tenderSource) {
        this.tenderSource = tenderSource;
    }

    @Override
    public String toString() {
        return "ExternalTender{" +
                "id=" + id +
                ", referenceNo='" + referenceNo + '\'' +
                ", title='" + title + '\'' +
                ", companyName='" + companyName + '\'' +
                ", publishedDate='" + publishedDate + '\'' +
                ", closingDate='" + closingDate + '\'' +
                ", status='" + status + '\'' +
                ", tenderURL='" + tenderURL + '\'' +
                ", tenderSource='" + tenderSource + '\'' +
                '}';
    }
}
