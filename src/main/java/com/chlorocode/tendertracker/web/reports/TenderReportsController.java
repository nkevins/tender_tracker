package com.chlorocode.tendertracker.web.reports;

import com.chlorocode.tendertracker.dao.dto.ProcurementReportDTO;
import com.chlorocode.tendertracker.service.notification.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public String showReportDetails(@RequestParam("reportType") String reportType, ModelMap model) throws ParseException {

        System.out.println("reportType: " + reportType);

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        List<ProcurementReportDTO> procurementReportList = reportService.findAllByDateRange(df.parse("01-06-2017"), df.parse("31-12-2017"));

        //for (int i=0; i<procurementReportList.size();i++)
            System.out.println(procurementReportList);

        model.addAttribute("selectedReportType", reportType);
        return "admin/reports/reportsDashboard";
    }
}
