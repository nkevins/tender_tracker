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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
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
    public void showReportDetails(@RequestParam("reportType") String reportType, ModelMap model,
                                    HttpServletRequest request, HttpServletResponse response) {

        System.out.println("reportType: " + reportType);
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        try {
            List<ProcurementReportDTO> procurementReportList = null;
            response.setContentType("application/ms-excel"); // or you can use text/csv
            response.setHeader("Content-Disposition", "attachment; filename=output.csv");
            OutputStream out = response.getOutputStream();
            String header = "Ref. No,Tender Name,Category,Tender Type,Start Date,End Date,Status\n";
            out.write(header.getBytes());
            procurementReportList = reportService.findAllByDateRange(df.parse(startDate), df.parse(endDate));
            for (ProcurementReportDTO procurementReportDTO : procurementReportList) {
                //System.out.println(procurementReportDTO.getRefNum() + " : " + procurementReportDTO.getTenderName() );
                String line=new String(procurementReportDTO.getRefNum()
                        +","+procurementReportDTO.getTenderName()
                        +","+procurementReportDTO.getTenderCategory()
                        +","+procurementReportDTO.getTenderType()
                        +","+procurementReportDTO.getOpeningDate()
                        +","+procurementReportDTO.getClosingDate()
                        +","+procurementReportDTO.getTenderStatus()
                        +"\n");

                out.write(line.toString().getBytes());
            }
            out.flush();
            out.close();
            model.addAttribute("selectedReportType", reportType);

        } catch (ParseException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            model.addAttribute("alert", alert);
        } catch (IOException e) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    e.getMessage());
        }

        //return "admin/reports/reportsDashboard";
    }

}
