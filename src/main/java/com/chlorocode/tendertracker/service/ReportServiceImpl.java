package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ReportDAO;
import com.chlorocode.tendertracker.dao.entity.ProcurementReport;
import com.chlorocode.tendertracker.service.notification.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by fistg on 17-Dec-17.
 */
@Service
public class ReportServiceImpl implements ReportService {

    private ReportDAO reportDAO;

    @Autowired
    public ReportServiceImpl(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    @Override
    public List<ProcurementReport> findAllByDateRange(String startDate, String endDate) {

        return reportDAO.findAllByDateRange(startDate, endDate);
    }
}
