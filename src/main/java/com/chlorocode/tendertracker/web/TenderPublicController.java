package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.TenderClarificationDTO;
import com.chlorocode.tendertracker.dao.dto.TenderItemResponseSubmitDTO;
import com.chlorocode.tendertracker.dao.dto.TenderResponseSubmitDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Controller
public class TenderPublicController {

    private TenderService tenderService;
    private BidService bidService;
    private CodeValueService codeValueService;
    private S3Wrapper s3Wrapper;
    private ClarificationService clariSvc;
    private CorrigendumService corrigendumService;

    @Autowired
    public TenderPublicController(TenderService tenderService, BidService bidService, CodeValueService codeValueService,
                                  S3Wrapper s3Wrapper,ClarificationService clariSvc, CorrigendumService corrigendumService) {
        this.tenderService = tenderService;
        this.bidService = bidService;
        this.codeValueService = codeValueService;
        this.s3Wrapper = s3Wrapper;
        this.clariSvc = clariSvc;
        this.corrigendumService = corrigendumService;
    }

    @GetMapping("/tender/{id}")
    public String showTenderDetails(@PathVariable(value="id") Integer id, ModelMap model) {
        Tender tender = tenderService.findById(id);
        if (tender == null) {
            return "redirect:/";
        }
        List<Clarification> lstClarification = clariSvc.findClarificationByTenderId(id);
        List<Corrigendum> lstCorrigendum = corrigendumService.findTenderCorrigendum(id);
        TenderClarificationDTO td = new TenderClarificationDTO();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            TenderBookmark tenderBookmark = tenderService.findTenderBookmark(tender.getId(), usr.getId());
            boolean isBookmarked;
            if (tenderBookmark == null) {
                isBookmarked = false;
            } else {
                isBookmarked = true;
            }
            model.addAttribute("isBookmarked", isBookmarked);
        }

        model.addAttribute("tender", tender);
        model.addAttribute("codeValueService", codeValueService);
        model.addAttribute("s3Service", s3Wrapper);
        model.addAttribute("clarification",lstClarification);
        model.addAttribute("corrigendums", lstCorrigendum);
        model.addAttribute("clarificationDto",td);
        return "tenderDetails";
    }

    @GetMapping("/tender/{id}/respond")
    public String showTenderResponsePage(@PathVariable(value = "id") Integer id, HttpServletRequest request,
                                         HttpServletResponse response, ModelMap model) {
        // Check access right
        if (!request.isUserInRole("ROLE_ADMIN") && !request.isUserInRole("ROLE_PREPARER")) {
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
            itm.setItemId(i.getId());

            data.addTenderItem(itm);
        }

        model.addAttribute("tender", tender);
        model.addAttribute("company", company);
        model.addAttribute("data", data);
        model.addAttribute("codeValueService", codeValueService);

        return "tenderResponse";
    }

    @PostMapping("/tender/respond")
    public String saveTenderResponse(@ModelAttribute("data") TenderResponseSubmitDTO data, HttpServletRequest request,
                                     HttpServletResponse resp, ModelMap model) throws IOException {
        String requestedWith = request.getHeader("X-Requested-With");
        Boolean isAjax = requestedWith != null && "XMLHttpRequest".equals(requestedWith);

        Tender tender = tenderService.findById(data.getTenderId());
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Bid bid = new Bid();
        bid.setCompany(usr.getSelectedCompany());
        bid.setTender(tender);
        bid.setCreatedBy(usr.getId());
        bid.setCreatedDate(new Date());
        bid.setLastUpdatedBy(usr.getId());
        bid.setLastUpdatedDate(new Date());

        for (TenderItemResponseSubmitDTO item : data.getItems()) {
            TenderItem tenderItem = tenderService.findTenderItemById(item.getItemId());

            BidItem bidItem = new BidItem();
            bidItem.setTenderItem(tenderItem);
            bidItem.setAmount(item.getQuotedPrice());
            bidItem.setCreatedBy(usr.getId());
            bidItem.setCreatedDate(new Date());
            bidItem.setLastUpdatedBy(usr.getId());
            bidItem.setLastUpdatedDate(new Date());

            bid.addBidItem(bidItem);
        }

        try
        {
            bidService.saveBid(bid, data.getAttachments());
        } catch(ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            model.addAttribute("alert", alert);
            model.addAttribute("tender", tender);
            model.addAttribute("company", usr.getSelectedCompany());
            model.addAttribute("data", data);
            model.addAttribute("codeValueService", codeValueService);

            if (isAjax) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print(ex.getMessage());
                return null;
            } else {
                return "tenderResponse";
            }
        }

        if (isAjax) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return null;
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/tender/bookmark")
    public String bookmarkTender(@RequestParam("tenderId") int tenderId) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tender tender = tenderService.findById(tenderId);

        tenderService.bookmarkTender(tender, usr.getUser());

        return "redirect:/tender/" + tenderId;
    }

    @PostMapping("/tender/removeBookmark")
    public String removeTenderBookmark(@RequestParam("tenderId") int tenderId) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        tenderService.removeTenderBookmark(tenderId, usr.getUser().getId());

        return "redirect:/tender/" + tenderId;
    }

    @GetMapping("/tenderNotification")
    public String showSubscribeTenderCategoryNotification(ModelMap model) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<TenderCategory> tenderCategories = codeValueService.getAllTenderCategories();
        List<TenderCategorySubscription> currentSubscription = tenderService.findUserSubscription(usr.getId());
        List<Integer> subscriptions = new LinkedList<>();
        for (TenderCategorySubscription s : currentSubscription) {
            subscriptions.add(s.getTenderCategory().getId());
        }

        model.addAttribute("categories", tenderCategories);
        model.addAttribute("currentSubscriptions", subscriptions);
        return "tenderNotification";
    }

    @PostMapping("/tenderNotification")
    public String saveTenderCategorySubscription(@RequestParam("categories") List<Integer> categories, RedirectAttributes redirectAttrs) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<TenderCategory> tenderCategoryList = new LinkedList<>();
        for (Integer i : categories) {
            tenderCategoryList.add(codeValueService.getTenderCategoryById(i));
        }

        tenderService.subscribeToTenderCategory(usr.getUser(), tenderCategoryList);

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "Subscription Added");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/tenderNotification";
    }
}
