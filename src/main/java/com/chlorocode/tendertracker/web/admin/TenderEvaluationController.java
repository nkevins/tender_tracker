package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.service.*;
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
import java.util.LinkedList;
import java.util.List;

@Controller
public class TenderEvaluationController {

    private EvaluationService evaluationService;
    private BidService bidService;
    private TenderService tenderService;
    private CodeValueService codeValueService;
    private S3Wrapper s3Wrapper;

    @Autowired
    public TenderEvaluationController(EvaluationService evaluationService, BidService bidService,
                                      TenderService tenderService, CodeValueService codeValueService, S3Wrapper s3Wrapper) {
        this.evaluationService = evaluationService;
        this.bidService = bidService;
        this.tenderService = tenderService;
        this.codeValueService = codeValueService;
        this.s3Wrapper = s3Wrapper;
    }

    @GetMapping("admin/tender/evaluation/{id}/bid")
    public String showTenderEvaluationBidListPage(@PathVariable(value="id") Integer id, ModelMap model) {
        Tender tender = tenderService.findById(id);
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("tender", tender);
        model.addAttribute("evaluationService", evaluationService);
        model.addAttribute("userid", usr.getId());
        return "admin/tender/tenderEvaluationBidList";
    }

    @GetMapping("admin/tender/submitEvaluation/{id}")
    public String showSubmitTenderEvaluationPage(@PathVariable(value="id") Integer id, ModelMap model) {
        Bid bid = bidService.findById(id);
        List<String> notifications = new LinkedList<>();
        List<EvaluationCriteria> criteria = evaluationService.findEvaluationCriteriaByTender(bid.getTender().getId());

        double totalBidAmount = 0;
        for (BidItem bi : bid.getBidItems()) {
            totalBidAmount += bi.getAmount();
        }

        if (totalBidAmount > bid.getTender().getEstimatePurchaseValue()) {
            notifications.add("Exceed EPV");
        }
        if (!bid.getCompany().isActive()) {
            notifications.add("Company is Blacklisted");
        }

        model.addAttribute("tender", bid.getTender());
        model.addAttribute("bid", bid);
        model.addAttribute("codeValueService", codeValueService);
        model.addAttribute("totalBidAmount", totalBidAmount);
        model.addAttribute("notifications", notifications);
        model.addAttribute("criteria", criteria);
        model.addAttribute("s3Service", s3Wrapper);
        return "admin/tender/tenderEvaluationSubmit";
    }

    @PostMapping("admin/tender/submitEvaluation/{id}")
    public String submitTenderEvaluation(@PathVariable(value="id") Integer id, WebRequest request, RedirectAttributes redirectAttrs) {
        Bid bid = bidService.findById(id);
        List<EvaluationCriteria> criteria = evaluationService.findEvaluationCriteriaByTender(bid.getTender().getId());
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<EvaluationResult> results = new LinkedList<>();
        for (EvaluationCriteria c : criteria) {
            EvaluationResult res = new EvaluationResult();
            res.setBid(bid);
            res.setResult(Integer.parseInt(request.getParameter("criteria_" + Integer.toString(c.getId()))));
            res.setCriteria(c);
            res.setEvaluator(usr.getUser());
            res.setCreatedBy(usr.getId());
            res.setCreatedDate(new Date());
            res.setLastUpdatedBy(usr.getId());
            res.setLastUpdatedDate(new Date());

            results.add(res);
        }

        try {
            evaluationService.saveEvaluationResult(results);
        } catch (Exception ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/tender/evaluation/" + bid.getTender().getId() + "/bid";
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Evaluation Submitted");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/evaluation/" + bid.getTender().getId() + "/bid";
    }
}
