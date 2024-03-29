package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.CompanyDAO;
import com.chlorocode.tendertracker.dao.TenderDAO;
import com.chlorocode.tendertracker.dao.dto.ProcurementReportDTO;
import com.chlorocode.tendertracker.dao.dto.ReportSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Service implementation of ReportService.
 */
@Service
public class ReportServiceImpl implements ReportService {

    private TenderDAO tenderDAO;
    private CompanyDAO companyDAO;

    /**
     * Constructor.
     *
     * @param tenderDAO TenderDAO
     * @param companyDAO CompanyDAO
     */
    @Autowired
    public ReportServiceImpl(TenderDAO tenderDAO, CompanyDAO companyDAO) {
        this.tenderDAO = tenderDAO;
        this.companyDAO = companyDAO;
    }

    @Override
    public List<ProcurementReportDTO> findAllByDateRange(Date openDateFrom, Date openDateTo,
                                                         Date closeDateFrom, Date closeDateTo,
                                                         String category, String status) {

        List<ProcurementReportDTO> results = new LinkedList<ProcurementReportDTO>();

        List<Object[]> rawResult = tenderDAO.findAllByDateRange(openDateFrom, openDateTo, closeDateFrom, closeDateTo,
                category, status);
        for (Object[] rows : rawResult) {
            ProcurementReportDTO row = new ProcurementReportDTO((String)rows[0], (String)rows[1], (String)rows[2],
                    (String)rows[3], (Date)rows[4], (Date)rows[5], (String)rows[6]);
            results.add(row);
        }

        return results;
    }

    @Override
    public List<ReportSummaryDTO> findTenderSummary(Date startDate, Date endDate) {
        List<Object[]> rawResult = tenderDAO.getTenderSummary(startDate, endDate);
        List<ReportSummaryDTO> results = new LinkedList<ReportSummaryDTO>();

        for (Object[] rows : rawResult) {
            ReportSummaryDTO row = new ReportSummaryDTO((String)rows[0], (String.valueOf((BigInteger)rows[1])));
            results.add(row);
        }
        return results;
    }

    @Override
    public List<ReportSummaryDTO> findCompanySummary(Date startDate, Date endDate) {
        List<Object[]> rawResult = companyDAO.getCompanySummary(startDate, endDate);
        List<ReportSummaryDTO> results = new LinkedList<ReportSummaryDTO>();

        for (Object[] rows : rawResult) {
            ReportSummaryDTO row = new ReportSummaryDTO((String)rows[0], (String.valueOf((BigInteger)rows[1])));
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
