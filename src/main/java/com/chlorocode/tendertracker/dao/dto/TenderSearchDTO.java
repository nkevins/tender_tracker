package com.chlorocode.tendertracker.dao.dto;

public class TenderSearchDTO {

    private String searchText;

    private String title;

    private String companyName;

    private int tenderCategory;

    private int status;

    private int tenderSource;

    private String etStatus;

    private String refNo;

    private String orderBy;

    private String orderMode;

    private boolean advance;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getTenderCategory() {
        return tenderCategory;
    }

    public void setTenderCategory(int tenderCategory) {
        this.tenderCategory = tenderCategory;
    }

    public void setTenderCategory(String tenderCategory) {
        this.tenderCategory = tenderCategory == null ? 0 : Integer.parseInt(tenderCategory);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = status == null ? 0 : Integer.parseInt(status);
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isAdvance() {
        return advance;
    }

    public void setAdvance(boolean advance) {
        this.advance = advance;
    }

    public String getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(String orderMode) {
        this.orderMode = orderMode;
    }

    public String getEtStatus() {
        return etStatus;
    }

    public void setEtStatus(String etStatus) {
        this.etStatus = etStatus;
    }

    public int getTenderSource() {
        return tenderSource;
    }

    public void setTenderSource(int tenderSource) {
        this.tenderSource = tenderSource;
    }
}
