package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ApplicationException;
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

/**
 * Controller for admin bid related page.
 */
@Controller
public class BidController {

    private BidService bidService;
    private TenderAppealService tenderAppealService;

    /**
     * Constructor.
     *
     * @param bidService BidService
     * @param tenderAppealService TenderAppealService
     */
    @Autowired
    public BidController(BidService bidService, TenderAppealService tenderAppealService) {
        this.bidService = bidService;
        this.tenderAppealService = tenderAppealService;
    }

    /**
     * This method is used for showing list of bid submitted by the company.
     *
     * @return String
     */
    @GetMapping("/admin/bid")
    public String showSubmittedBidPage() {
        return "admin/bid/bidView";
    }

    /**
     * This method is used for showing submit tender appeal page.
     *
     * @param id unique identifier of the bid
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/admin/bid/appeal/{id}")
    public String showTenderAppealPage(@PathVariable(value="id") Integer id, ModelMap model) {
        Bid bid = bidService.findById(id);

        model.addAttribute("bid", bid);

        return "admin/bid/submitBidAppeal";
    }

    /**
     * This method is used to save tender appeal.
     *
     * @param tenderId unique identifier of the tender
     * @param reason appeal reason
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/admin/bid/appeal/submit")
    public String submitAppeal(@RequestParam("id") int tenderId, @RequestParam("reason") String reason, RedirectAttributes redirectAttrs){
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Company company = usr.getSelectedCompany();

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

        TenderAppeal result;
        try {
            result = tenderAppealService.create(tender);
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/bid" ;
        }

        if(result != null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                    "Appeal submitted successfully");
            redirectAttrs.addFlashAttribute("alert", alert);
        }else{
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Not able to submit the appeal. Please contact Administrator");
            redirectAttrs.addFlashAttribute("alert", alert);
        }

        return "redirect:/admin/bid" ;
    }

    /**
     * This method is used for showing list of submitted tender appeal to be processed by admin.
     *
     * @return String
     */
    @GetMapping("/admin/tender/appeal")
    public String showTenderAppealPage() {
        return "admin/bid/tenderAppealList";
    }

    /**
     * This method is used for showing process appeal page.
     *
     * @param id unique identifier of the tender appeal
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/admin/tender/appeal/view/{id}")
    public String showTenderAppealUpdatePage(@PathVariable(value = "id") Integer id,ModelMap model) {
        TenderAppeal tenderAppeal = tenderAppealService.findById(id);

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

    /**
     * This method is used for updating appeal status.
     *
     * @param id unique identifier of the tender appeal
     * @param action action indicator whether approve / reject
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/admin/bid/appeal/result")
    public String processAppeal(@RequestParam("id") int id, @RequestParam("action") String action,
                                   RedirectAttributes redirectAttrs) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean result = false;

        if (action.equals("reject")) {
             result = tenderAppealService.processTenderAppeal(id,usr.getId(),2);
        } else if(action.equals("approve")) {
             result = tenderAppealService.processTenderAppeal(id,usr.getId(),1);
        }

        if (result) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                    "Tender appeal status updated successfully");
            redirectAttrs.addFlashAttribute("alert", alert);
        } else {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                    "Something went wrong. Cannot update tender process");
            redirectAttrs.addFlashAttribute("alert", alert);
        }

        return "redirect:/admin/tender/appeal";
    }
}
