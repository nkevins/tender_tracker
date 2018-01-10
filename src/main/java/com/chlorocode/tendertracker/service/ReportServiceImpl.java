package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.TenderDAO;
import com.chlorocode.tendertracker.dao.dto.ProcurementReportDTO;
import com.chlorocode.tendertracker.service.notification.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by fistg on 17-Dec-17.
 */
@Service
public class ReportServiceImpl implements ReportService {

    private TenderDAO tenderDAO;

    @Autowired
    public ReportServiceImpl(TenderDAO tenderDAO) {
        this.tenderDAO = tenderDAO;
    }

    @Override
    public List<ProcurementReportDTO> findAllByDateRange(Date startDate, Date endDate) {

        List<ProcurementReportDTO> results = new LinkedList<>();

        List<Object[]> rawResult = tenderDAO.findAllByDateRange(startDate, endDate);
        for (Object[] rows : rawResult) {
            ProcurementReportDTO row = new ProcurementReportDTO((String)rows[0], (String)rows[1], (String)rows[2],
                    (String)rows[3], (Date)rows[4], (Date)rows[5], (String)rows[6]);
            results.add(row);
        }

        return results;
    }

    @Override
    public Integer getNumberOfVisit(int tenderId) {
        return tenderDAO.getNumberOfVisit(tenderId);
    }

    @Override
    public Integer getNumberOfUniqueVisit(int tenderId) {
        return tenderDAO.getNumberOfUniqueVisit(tenderId);
    }

    @Override
    public List<Object[]> getTopCountryVisitor(int tenderId) {
        return tenderDAO.getTopCountryVisitor(tenderId, new PageRequest(0, 5));
    }
}
