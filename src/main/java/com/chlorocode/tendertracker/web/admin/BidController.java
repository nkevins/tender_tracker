package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.service.BidService;
import com.chlorocode.tendertracker.service.TenderAppealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

@Controller
public class BidController {

    BidService bidService;
    TenderAppealService tdSvc;

    @Autowired
    public BidController(BidService bidService, TenderAppealService tdSvc1) {
        this.bidService = bidService;
        this.tdSvc = tdSvc1;
    }

    @GetMapping("/admin/bid")
    public String showTenderPage() {
        return "admin/bid/bidView";
    }

    @GetMapping("/admin/tender/appeal")
    public String showTenderAppealPage() {
        return "admin/bid/tenderAppealList";
    }

    @PostMapping("/admin/bid/appeal/result")
    public String ProcessAppeal(@RequestParam("id") int id, @RequestParam("action") String action,
                                   RedirectAttributes redirectAttrs) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean result = false;

        if (action.equals("reject")) {
             result = tdSvc.processTender(id,usr.getId(),2);
        }else if(action.equals("approve")){
             result = tdSvc.processTender(id,usr.getId(),1);
        }

        if(result){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                    "Tender appeal status updated successfully");
            redirectAttrs.addFlashAttribute("alert", alert);
        }else{
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                    "Something went wrong. Cannot update tender process");
            redirectAttrs.addFlashAttribute("alert", alert);
        }

        return "redirect:/admin/tender/appeal";
    }

    @GetMapping("/admin/tender/appeal/view/{id}")
    public String showTenderAppealUpdatePage(@PathVariable(value = "id") Integer id,ModelMap model) {
        TenderAppeal tenderAppeal = tdSvc.findById(id);

        String status = "";
        if (tenderAppeal.getStatus() == 0) {
            status = "Pending";
        } else if (tenderAppeal.getStatus() == 1) {
            status = "Approved";
        } else if (tenderAppeal.getStatus() == 2) {
            status = "Rejected";
        }

        model.addAttribute("bid", tenderAppeal);
        model.addAttribute("status", status);
        return "admin/bid/processAppealView";
    }

    @GetMapping("/admin/bid/appeal/{id}")
    public String appealTender(@PathVariable(value="id") Integer id, ModelMap model) {
        Bid bid = bidService.findById(id);

        model.addAttribute("bid", bid);

        return "admin/bid/submitBidAppeal";
    }

    @PostMapping("/admin/bid/appeal/submit")
    public String submitAppeal (@RequestParam("id") int tenderId, @RequestParam("reason") String reason,ModelMap model){
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Company company = usr.getSelectedCompany();
        List<TenderAppeal> appeal = tdSvc.findTenderAppealsBy(tenderId,company.getId());

        if(appeal != null && appeal.size() > 0){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Not allow to submit duplicate appeal.");
            model.addAttribute("alert", alert);
            return "redirect:/admin/bid" ;
        }

        TenderAppeal tender = new TenderAppeal();
        Tender td = new Tender();
        td.setId(tenderId);

        tender.setTender(td);
        tender.setCompany(company);
        tender.setReasons(reason);
        tender.setCreatedBy(usr.getId());
        tender.setLastUpdatedBy(usr.getId());
        tender.setCreatedDate(new Date());
        tender.setLastUpdatedDate(new Date());

        TenderAppeal result = tdSvc.Create(tender);

        if(result != null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                    "Appeal submitted successfully");
            model.addAttribute("alert", alert);
        }else{
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Not able to submit the appeal. Please contact Administrator");
            model.addAttribute("alert", alert);
        }

        return "redirect:/admin/bid" ;
    }
}
