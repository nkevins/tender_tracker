package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.ProcurementReportDTO;
import com.chlorocode.tendertracker.dao.dto.ReportSummaryDTO;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.TenderService;
import com.chlorocode.tendertracker.service.ReportService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public static final DateFormat DATEFORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final String CSV_DELIMITER = ",";

    private ReportService reportService;
    private CodeValueService codeValueService;
    private TenderService tenderService;

    @Autowired
    public TenderReportsController(ReportService reportService, CodeValueService codeValueService, TenderService tenderService) {
        this.reportService = reportService;
        this.codeValueService = codeValueService;
        this.tenderService = tenderService;
    }

    @GetMapping("/admin/report/procurementreport")
    public String viewReportsPage(ModelMap model){
        model.addAttribute("tenderType", codeValueService.getByType("tender_type"));
        model.addAttribute("tenderCategories", codeValueService.getAllTenderCategories());
        return "admin/reports/procurementreport";
    }

    @PostMapping("/admin/report/procurementreport")
    public void downloadProcurementReport(ModelMap model,
                                          HttpServletRequest request, HttpServletResponse response) {

        String openingDateFrom = request.getParameter("openingDateFrom");
        String openingDateTo = request.getParameter("openingDateTo");
        String closingDateFrom = request.getParameter("closingDateFrom");
        String closingDateTo = request.getParameter("closingDateTo");
        String category = request.getParameter("category");
        String status = request.getParameter("status");
        String fileType = request.getParameter("fileType");
        //if(status.isEmpty()){
        //    status="%";
       // }
        System.out.println(openingDateFrom);
        System.out.println(openingDateTo);
        System.out.println(closingDateFrom);
        System.out.println(closingDateTo);
        System.out.println(category);
        System.out.println(status);
        try {
            List<ProcurementReportDTO> procurementReportList = null;
            procurementReportList = reportService.findAllByDateRange(
                    DATEFORMAT.parse(openingDateFrom), DATEFORMAT.parse(openingDateTo),
                    DATEFORMAT.parse(closingDateFrom), DATEFORMAT.parse(closingDateTo),
                    category, status);
            if(fileType.equals("pdf")) {
                downloadProcurementReportPdf(response, procurementReportList);
            }
            if(fileType.equals("csv")){
                downloadProcurementReportCSV(response, procurementReportList);
            }

        } catch (ParseException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            model.addAttribute("alert", alert);
        } catch (IOException e) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    e.getMessage());
        }

    }

    private void downloadProcurementReportCSV(HttpServletResponse response, List<ProcurementReportDTO> procurementReportList) throws IOException {
        response.setContentType("application/ms-excel"); // or you can use text/csv
        response.setHeader("Content-Disposition", "attachment; filename=procurementreport.csv");
        OutputStream out = response.getOutputStream();
        out.write(PROCUREMENTREPORT_HEADER.getBytes());
        for (ProcurementReportDTO procurementReportDTO : procurementReportList) {
            String line=new String(procurementReportDTO.getRefNum()
                    + CSV_DELIMITER +procurementReportDTO.getTenderName()
                    + CSV_DELIMITER +procurementReportDTO.getTenderCategory()
                    + CSV_DELIMITER +procurementReportDTO.getTenderType()
                    + CSV_DELIMITER +DATEFORMAT.format(procurementReportDTO.getOpeningDate())
                    + CSV_DELIMITER +DATEFORMAT.format(procurementReportDTO.getClosingDate())
                    + CSV_DELIMITER +procurementReportDTO.getTenderStatus()
                    +"\n");

            out.write(line.toString().getBytes());
        }
        out.flush();
        out.close();
    }

    private void downloadProcurementReportPdf(HttpServletResponse response,
                                              List<ProcurementReportDTO> procurementReportList) throws IOException {

        OutputStream out = response.getOutputStream();
        // Apply preferences and build metadata
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=procurementreport.pdf");
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDocument = new PdfDocument(writer);

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Tender Procurement Report"));
        Table table = new Table(8);
        table.addHeaderCell("Sl. No");
        table.addHeaderCell("Ref. No");
        table.addHeaderCell("Tender Name");
        table.addHeaderCell("Category");
        table.addHeaderCell("Tender Type");
        table.addHeaderCell("Start Date");
        table.addHeaderCell("End Date");
        table.addHeaderCell("Status");
        int slNo = 1;
        for (ProcurementReportDTO procurementReportDTO : procurementReportList) {
            table.addCell(String.valueOf(slNo++));
            table.addCell(procurementReportDTO.getRefNum());
            table.addCell(procurementReportDTO.getTenderName());
            table.addCell(procurementReportDTO.getTenderCategory());
            table.addCell(procurementReportDTO.getTenderType());
            table.addCell(DATEFORMAT.format(procurementReportDTO.getOpeningDate()));
            table.addCell(DATEFORMAT.format(procurementReportDTO.getClosingDate()));
            table.addCell(procurementReportDTO.getTenderStatus());
        }
        document.add(table);
        document.close();

        response.setHeader("Content-Disposition", "attachment");    // make browser to ask for download/display
        out.flush();
        out.close();
    }


    @GetMapping("/admin/report/statisticsreport")
    public String viewStatisticsReportsPage(ModelMap model){
        model.addAttribute("tenderType", codeValueService.getByType("tender_type"));
        model.addAttribute("tenderCategories", codeValueService.getAllTenderCategories());
        return "admin/reports/statisticsreport";
    }

    @PostMapping("/admin/report/statisticsreport")
    public String downloadStatisticsReport(ModelMap model,
                                          HttpServletRequest request, HttpServletResponse response) {

        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        try {
            model.addAttribute("fromDate",fromDate);
            model.addAttribute("toDate",toDate);
            model.addAttribute("tenderSummary", reportService.findTenderSummary(
                    DATEFORMAT.parse(fromDate),DATEFORMAT.parse(toDate)));
            model.addAttribute("companySummary", reportService.findCompanySummary(
                    DATEFORMAT.parse(fromDate),DATEFORMAT.parse(toDate)));
            System.out.println(reportService.findTenderSummary(
                    DATEFORMAT.parse(fromDate),DATEFORMAT.parse(toDate)));
        } catch (ParseException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            model.addAttribute("alert", alert);
        }

        return "admin/reports/statisticsreport";

        /*
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            List<ReportSummaryDTO> reportSummaryDTOList = null;
            response.setContentType("application/ms-excel"); // or you can use text/csv
            response.setHeader("Content-Disposition", "attachment; filename=statisticsreport.csv");
            OutputStream out = response.getOutputStream();
            out.write(PROCUREMENTREPORT_HEADER.getBytes());
            reportSummaryDTOList = reportService.findTenderAndCompanySummary(df.parse(fromDate), df.parse(toDate));

            for (ReportSummaryDTO reportSummaryDTOList : reportSummaryDTOList) {


                //out.write(line.toString().getBytes());
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
        */

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
