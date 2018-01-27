package com.chlorocode.tendertracker.dao.dto;

import java.util.Date;

/**
 * Created by Rag on 17-Dec-17.
 * Data transfer object for procurement report.
 */
public class ProcurementReportDTO {

    private String refNum;
    private String tenderName;
    private String tenderType;
    private String tenderCategory;
    private Date openingDate;
    private Date closingDate;
    private String tenderStatus;

    public ProcurementReportDTO(String refNum, String tenderName, String tenderType, String tenderCategory, Date openingDate, Date closingDate, String tenderStatus) {
        this.refNum = refNum;
        this.tenderName = tenderName;
        this.tenderType = tenderType;
        this.tenderCategory = tenderCategory;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.tenderStatus = tenderStatus;
    }

    public String getRefNum() { return refNum; }

    public void setRefNum(String refNum) { this.refNum = refNum; }

    public String getTenderName() {
        return tenderName;
    }

    public void setTenderName(String tenderName) {
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
