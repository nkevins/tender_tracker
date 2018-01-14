package com.chlorocode.tendertracker.dao.dto;

import java.util.Date;

/**
 * Created by fistg on 17-Dec-17.
 */
public class ReportSummaryDTO {

    private String status;
    private String count;

    public ReportSummaryDTO(String status, String count) {
        this.status = status;
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
