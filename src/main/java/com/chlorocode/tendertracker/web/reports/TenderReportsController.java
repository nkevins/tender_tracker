package com.chlorocode.tendertracker.web.reports;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.ProcurementReportDTO;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.TenderService;
import com.chlorocode.tendertracker.service.notification.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    public static final String PROCUREMENTREPORT_HEADER = "Ref. No,Tender Name,Category,Tender Type,Start Date,End Date,Status\n";
    private ReportService reportService;
    private CodeValueService codeValueService;
    private TenderService tenderService;

    @Autowired
    public TenderReportsController(ReportService reportService, CodeValueService codeValueService, TenderService tenderService) {
        this.reportService = reportService;
        this.codeValueService = codeValueService;
        this.tenderService = tenderService;
    }

    @GetMapping("/admin/tender/procurementreport")
    public String viewReportsPage(ModelMap model){
        model.addAttribute("tenderType", codeValueService.getByType("tender_type"));
        model.addAttribute("tenderCategories", codeValueService.getAllTenderCategories());
        return "admin/reports/procurementreport";
    }

    @PostMapping("/admin/tender/procurementreport")
    public void downloadProcurementReport(ModelMap model,
                                          HttpServletRequest request, HttpServletResponse response) {

        String openingDateFrom = request.getParameter("openingDateFrom");
        String openingDateTo = request.getParameter("openingDateTo");
        String closingDateFrom = request.getParameter("closingDateFrom");
        String closingDateTo = request.getParameter("closingDateTo");
        String category = request.getParameter("category");
        String status = request.getParameter("status");

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        try {
            List<ProcurementReportDTO> procurementReportList = null;
            response.setContentType("application/ms-excel"); // or you can use text/csv
            response.setHeader("Content-Disposition", "attachment; filename=procurementreport.csv");
            OutputStream out = response.getOutputStream();
            out.write(PROCUREMENTREPORT_HEADER.getBytes());
            procurementReportList = reportService.findAllByDateRange(df.parse(openingDateFrom), df.parse(openingDateTo));
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

        } catch (ParseException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            model.addAttribute("alert", alert);
        } catch (IOException e) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    e.getMessage());
        }

    }


    @GetMapping("/admin/tender/statisticsreport")
    public String viewStatisticsReportsPage(ModelMap model){
        model.addAttribute("tenderType", codeValueService.getByType("tender_type"));
        model.addAttribute("tenderCategories", codeValueService.getAllTenderCategories());
        return "admin/reports/statisticsreport";
    }

    @PostMapping("/admin/tender/statisticsreport")
    public void downloadStatisticsReport(ModelMap model,
                                          HttpServletRequest request, HttpServletResponse response) {

        //System.out.println("reportType: " + reportType);
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        try {
            List<ProcurementReportDTO> procurementReportList = null;
            response.setContentType("application/ms-excel"); // or you can use text/csv
            response.setHeader("Content-Disposition", "attachment; filename=statisticsreport.csv");
            OutputStream out = response.getOutputStream();
            out.write(PROCUREMENTREPORT_HEADER.getBytes());
            procurementReportList = reportService.findAllByDateRange(df.parse(fromDate), df.parse(toDate));
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

        } catch (ParseException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            model.addAttribute("alert", alert);
        } catch (IOException e) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    e.getMessage());
        }

    }

    @GetMapping("/admin/report/analyticreport")
    public String viewAnalyticReportList() {
        return "admin/reports/analyticReport";
    }

    @GetMapping("/admin/report/analyticreport/{id}")
    public String viewAnalyticReportDetail(@PathVariable(value="id") int id, ModelMap model) {
        model.addAttribute("tender", tenderService.findById(id));
        model.addAttribute("visit", reportService.getNumberOfVisit(id));
        model.addAttribute("uniqueVisit", reportService.getNumberOfUniqueVisit(id));
        model.addAttribute("topCountryVisit", reportService.getTopCountryVisitor(id));

        return "admin/reports/analyticReportDetails";
    }
}
