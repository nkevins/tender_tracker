package com.chlorocode.tendertracker.web.reports;

import com.chlorocode.tendertracker.dao.ReportDAO;
import com.chlorocode.tendertracker.dao.entity.ProcurementReport;
import com.chlorocode.tendertracker.service.ReportServiceImpl;
import com.chlorocode.tendertracker.service.notification.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by rags on 10-Dec-17.
 */
@Controller
public class TenderReportsController {

    private ReportService reportService;

    @Autowired
    public TenderReportsController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/admin/tender/reports")
    public String viewReportsPage(ModelMap model){

        return "admin/reports/reportsDashboard";
    }

    @PostMapping("/admin/tender/reportdetails")
    public String showReportDetails(@RequestParam("reportType") String reportType, ModelMap model){

        System.out.println("reportType: " + reportType);

        //ReportDAO reportDAO = new ReportDAO
        //ReportService reportService = new ReportServiceImpl(reportDAO);
        List<ProcurementReport> procurementReportList = reportService.findAllByDateRange("2017-07-01","2017-12-01");

        //for (int i=0; i<procurementReportList.size();i++)
            System.out.println(procurementReportList);

        model.addAttribute("selectedReportType", reportType);
        return "admin/reports/reportsDashboard";
    }
}
