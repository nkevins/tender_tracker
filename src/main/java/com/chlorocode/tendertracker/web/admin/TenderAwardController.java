package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.entity.Bid;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderAward;
import com.chlorocode.tendertracker.service.BidService;
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

@Controller
public class TenderAwardController {

    private TenderService tenderService;
    private BidService bidService;

    @Autowired
    public TenderAwardController(TenderService tenderService, BidService bidService) {
        this.tenderService = tenderService;
        this.bidService = bidService;
    }

    @GetMapping("/admin/tender/award")
    public String showTenderAwardListPage() {
        return "admin/tender/tenderAwardList";
    }

    @GetMapping("/admin/tender/award/{id}")
    public String showTenderAwardPage(@PathVariable(value="id") Integer id, ModelMap model) {
        Tender tender = tenderService.findById(id);

        model.addAttribute("tender", tender);

        return "admin/tender/tenderAward";
    }

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
