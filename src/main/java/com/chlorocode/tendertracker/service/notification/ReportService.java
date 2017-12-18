package com.chlorocode.tendertracker.service.notification;

import com.chlorocode.tendertracker.dao.entity.ProcurementReport;

import java.util.List;

/**
 * Created by fistg on 17-Dec-17.
 */
public interface ReportService {
    List<ProcurementReport> findAllByDateRange(String startDate, String endDate);

}
