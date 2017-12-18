package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.Column;
import java.util.Date;

/**
 * Created by fistg on 17-Dec-17.
 */
public class ProcurementReport {

    @Column(name = "ref_no")
    private String refNum;

    @Column(name = "title")
    private int tenderName;

    @Column(name = "tender_type")
    private String tenderType;

    @Column(name = "tender_categoryname")
    private String tenderCategory;

    @Column(name = "open_date")
    private Date openingDate;

    @Column(name = "closed_date")
    private Date closingDate;

    @Column(name = "status")
    private String tenderStatus;

    public String getRefNum() { return refNum; }

    public void setRefNum(String refNum) { this.refNum = refNum; }

    public int getTenderName() {
        return tenderName;
    }

    public void setTenderName(int tenderName) {
        this.tenderName = tenderName;
    }

    public String getTenderType() {
        return tenderType;
    }

    public void setTenderType(String tenderType) {
        this.tenderType = tenderType;
    }

    public String getTenderCategory() {
        return tenderCategory;
    }

    public void setTenderCategory(String tenderCategory) {
        this.tenderCategory = tenderCategory;
    }

    public Date getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Date openingDate) {
        this.openingDate = openingDate;
    }

    public Date getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate;
    }

    public String getTenderStatus() {
        return tenderStatus;
    }

    public void setTenderStatus(String tenderStatus) {
        this.tenderStatus = tenderStatus;
    }

}
