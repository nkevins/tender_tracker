package com.chlorocode.tendertracker.service.notification;

import com.chlorocode.tendertracker.dao.dto.ProcurementReportDTO;

import java.util.Date;
import java.util.List;

/**
 * Created by fistg on 17-Dec-17.
 */
public interface ReportService {
    List<ProcurementReportDTO> findAllByDateRange(Date startDate, Date endDate);

}
