package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.TenderItemResponseSubmitDTO;
import com.chlorocode.tendertracker.dao.dto.TenderResponseSubmitDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.BidService;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.TenderAppealService;
import com.chlorocode.tendertracker.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Controller for admin bid related page.
 */
@Controller
public class BidController {

    private BidService bidService;
    private TenderAppealService tenderAppealService;
    private TenderService tenderService;
    private CodeValueService codeValueService;

    /**
     * Constructor.
     *
     * @param bidService BidService
     * @param tenderAppealService TenderAppealService
     */
    @Autowired
    public BidController(BidService bidService, TenderAppealService tenderAppealService,TenderService tenderService,CodeValueService codeValueService) {
        this.bidService = bidService;
        this.tenderAppealService = tenderAppealService;
        this.tenderService = tenderService;
        this.codeValueService = codeValueService;
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


    /**
     * This method is used for showing tender response screen.
     *
     * @param id unique identifier of the tender
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/admin/bid/amend/{id}")
    public String amendBid(@PathVariable(value = "id") Integer id, HttpServletRequest request,
                           HttpServletResponse response, ModelMap model) {
        // Check access right
        if (!request.isUserInRole("ROLE_ADMIN") && !request.isUserInRole("ROLE_SUBMITTER")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        // Check if tender exist
        Tender tender = tenderService.findById(id);
        if (tender == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Company company = usr.getSelectedCompany();

        TenderResponseSubmitDTO data = new TenderResponseSubmitDTO();
        for (TenderItem i : tender.getItems()) {
            TenderItemResponseSubmitDTO itm = new TenderItemResponseSubmitDTO();
            itm.setItem(i);
            itm.setItemId(i.getBidItems().get(0).getId());
            BidItem bidI = bidService.findBidItemById(i.getBidItems().get(0).getId());
            itm.setQuotedPrice(bidI.getAmount());

            data.addTenderItem(itm);
        }

        model.addAttribute("tender", tender);
        model.addAttribute("company", company);
        model.addAttribute("data", data);
        model.addAttribute("codeValueService", codeValueService);
        model.addAttribute("currency",codeValueService.getByType("currency"));

        return "admin/tender/amendBid";
    }

    /**
     * This method is used to save the tender response.
     * This method can handle request both from normal HTTP and AJAX.
     * This method will return the name of next screen or null for AJAX response.
     * For AJAX response, this method will return the HTTP status and any error in the HTTP body.
     *
     * @param data input data of tender response
     * @param request HttpServletRequest
     * @param resp HttpServletResponse
     * @param model ModelMap
     * @return String
     * @throws IOException if has exception when putting error message in AJAX response
     * @see TenderResponseSubmitDTO
     */
    @PostMapping("/tender/respond/amend")
    public String AmendTenderResponse(@ModelAttribute("data") TenderResponseSubmitDTO data, HttpServletRequest request,
                                      HttpServletResponse resp, ModelMap model) throws IOException {

        Tender tender = tenderService.findById(data.getTenderId());
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try
        {
            for (TenderItemResponseSubmitDTO item : data.getItems()) {
                BidItem bidItem = bidService.findBidItemById(item.getItemId());
                bidItem.setAmount(item.getQuotedPrice());
                bidItem.setCreatedBy(usr.getId());
                bidItem.setCreatedDate(new Date());
                bidItem.setLastUpdatedBy(usr.getId());
                bidItem.setLastUpdatedDate(new Date());
                bidItem.setCurrency(item.getCurrency());
                bidService.updateBid(bidItem);
                AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                        "Tender appeal status updated successfully");
                model.addAttribute("alert", alert);
                // bid.addBidItem(bidItem);
            }
        } catch(ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            model.addAttribute("alert", alert);
            model.addAttribute("tender", tender);
            model.addAttribute("company", usr.getSelectedCompany());
            model.addAttribute("data", data);
            model.addAttribute("codeValueService", codeValueService);
        }

        return "redirect:/admin/bid";
    }
}
