package com.chlorocode.tendertracker.web.reports;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.ProcurementReportDTO;
import com.chlorocode.tendertracker.service.notification.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping("/admin/tender/reports")
    public String showReportDetails(@RequestParam("reportType") String reportType, ModelMap model,
                                    HttpServletRequest request) {

        System.out.println("reportType: " + reportType);
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        //System.out.println("startDate: " + request.getParameter("startDate"));
        //System.out.println("endDate: " + request.getParameter("endDate"));

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        List<ProcurementReportDTO> procurementReportList = null;
        try {
            procurementReportList = reportService.findAllByDateRange(df.parse(startDate), df.parse(endDate));
            for (ProcurementReportDTO procurementReportDTO : procurementReportList) {
                System.out.println(procurementReportDTO.getRefNum() + " : " + procurementReportDTO.getTenderName() );
            }
            model.addAttribute("selectedReportType", reportType);

        } catch (ParseException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            model.addAttribute("alert", alert);
        }

        //for (int i=0; i<procurementReportList.size();i++)
            //System.out.println(procurementReportList);

        return "admin/reports/reportsDashboard";
    }
}
