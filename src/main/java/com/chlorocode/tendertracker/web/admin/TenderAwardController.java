package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.service.BidService;
import com.chlorocode.tendertracker.service.EvaluationService;
import com.chlorocode.tendertracker.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

/**
 * Controller for tender award page in admin portal.
 */
@Controller
public class TenderAwardController {

    private TenderService tenderService;
    private BidService bidService;
    private EvaluationService evaluationService;

    /**
     * Constructor.
     *
     * @param tenderService TenderService
     * @param bidService BidService
     * @param evaluationService EvaluationService
     */
    @Autowired
    public TenderAwardController(TenderService tenderService, BidService bidService, EvaluationService evaluationService) {
        this.tenderService = tenderService;
        this.bidService = bidService;
        this.evaluationService = evaluationService;
    }

    /**
     * This method is used to display page containing the list of tenders to be awarded.
     *
     * @return String
     */
    @GetMapping("/admin/tender/award")
    public String showTenderAwardListPage() {
        return "admin/tender/tenderAwardList";
    }

    /**
     * This method is used to display award tender page.
     *
     * @param id unique identifier of the tender
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/admin/tender/award/{id}")
    public String showTenderAwardPage(@PathVariable(value="id") Integer id, ModelMap model) {
        Tender tender = tenderService.findById(id);

        List<EvaluationCriteria> criteria = evaluationService.findEvaluationCriteriaByTender(id);
        String criteriaList = "";
        for (EvaluationCriteria c : criteria) {
            criteriaList += c.getId() + ",";
        }
        if (criteriaList.length() > 0) {
            criteriaList = criteriaList.substring(0, criteriaList.length() - 1);
        }

        model.addAttribute("tender", tender);
        model.addAttribute("evaluationCriteria", criteria);
        model.addAttribute("evaluationCriteraList", criteriaList);

        return "admin/tender/tenderAward";
    }

    /**
     * This method is used to save tender award.
     *
     * @param id unique identifier of the bid
     * @param request WebRequest
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/admin/tender/award/{id}")
    public String saveTenderAward(@PathVariable(value="id") Integer id, WebRequest request, RedirectAttributes redirectAttrs) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Bid bid = bidService.findById(Integer.parseInt(request.getParameter("awardTo")));

        TenderAward tenderAward = new TenderAward();
        tenderAward.setBid(bid);
        tenderAward.setTender(bid.getTender());
        tenderAward.setCompany(bid.getCompany());
        tenderAward.setCreatedBy(usr.getId());
        tenderAward.setCreatedDate(new Date());
        tenderAward.setLastUpdatedBy(usr.getId());
        tenderAward.setLastUpdatedDate(new Date());

        try {
            tenderService.awardTender(tenderAward);
        } catch (Exception ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/tender/award/" + id;
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Tender Awarded");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/award";
    }
}
