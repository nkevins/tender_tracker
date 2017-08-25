package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.dto.*;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.Optional;

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
            Bid bid = null;
            boolean isBookmarked;
            boolean isSubmitedTender;

            if(usr.getSelectedCompany() != null)
            {
                bid = bidService.findBidByCompanyAndTender(usr.getSelectedCompany().getId(),id);
            }

            if (tenderBookmark == null) {
                isBookmarked = false;
            } else {
                isBookmarked = true;
            }

            if(bid == null){
                isSubmitedTender = false;
            }else{
                isSubmitedTender = true;
            }
            model.addAttribute("isBookmarked", isBookmarked);
            model.addAttribute("isSubmitedTender", isSubmitedTender);
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
            itm.setItemId(i.getId());

            data.addTenderItem(itm);
        }

        model.addAttribute("tender", tender);
        model.addAttribute("company", company);
        model.addAttribute("data", data);
        model.addAttribute("codeValueService", codeValueService);
        model.addAttribute("currency",codeValueService.getByType("currency"));

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
            bidItem.setCurrency(item.getCurrency());

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

//    /**
//     * Handles all requests
//     *
//     * @param pageSize
//     * @param page
//     * @return model and view
//     */
//    @PostMapping("/tender/search")
//    public String showPersonsPage(@RequestParam("pageSize") Optional<Integer> pageSize
//                                        , @RequestParam("page") Optional<Integer> page
//                                        , @ModelAttribute("searchCriteria") TenderSearchDTO form
//                                        , ModelMap model) {
//        // Evaluate page size. If requested parameter is null, return initial
//        // page size
//        int evalPageSize = pageSize.orElse(TTConstants.INITIAL_PAGE_SIZE);
//        // Evaluate page. If requested parameter is null or less than 0 (to
//        // prevent exception), return initial size. Otherwise, return value of
//        // param. decreased by 1.
//        int evalPage = (page.orElse(0) < 1) ? TTConstants.INITIAL_PAGE : page.get() - 1;
//
//        Page<Tender> tenders = tenderService.searchTender(form
//                , new PageRequest(
//                        evalPage, evalPageSize, new Sort(new Sort.Order(Sort.Direction.ASC, TTConstants.OPEN_DATE))
//                ));
//        Pager pager = new Pager(tenders.getTotalPages(), tenders.getNumber(), TTConstants.BUTTONS_TO_SHOW);
//
//        form.setOrderBy(TTConstants.OPEN_DATE);
//        model.addAttribute("tenders", tenders);
//        model.addAttribute("searchCriteria", form);
//        model.addAttribute("codeValueSvc", codeValueService);
//        model.addAttribute("selectedPageSize", evalPageSize);
////        modelAndView.addObject("pageSizes", PAGE_SIZES);
//        model.addAttribute("pager", pager);
//        if (tenders == null || tenders.getTotalPages() == 0) {
//            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.WARNING,
//                    "No tenders found.");
//            model.addAttribute("alert", alert);
//        }
//        return "home";
//    }

    @ModelAttribute("searchCriteria")
    public TenderSearchDTO getTenderSearchDTO(HttpServletRequest request)
    {
        return (TenderSearchDTO) request.getAttribute("searchCriteria");
    }

    /**
     * Handles all requests
     *
     * @param pageSize
     * @param page
     * @return model and view
     */
    @GetMapping("/tenders")
    public String showPersonsPageWithOrder(@RequestParam("pageSize") Optional<Integer> pageSize
            , @RequestParam("page") Optional<Integer> page
            , @RequestParam("searchText") Optional<String> searchText
            , @RequestParam("title") Optional<String> title
            , @RequestParam("companyName") Optional<String> companyName
            , @RequestParam("tenderCategory") Optional<Integer> tenderCategory
            , @RequestParam("status") Optional<Integer> status
            , @RequestParam("refNo") Optional<String> refNo
            , @RequestParam("orderBy") Optional<String> orderBy
            , ModelMap model) {
        // Evaluate page size. If requested parameter is null, return initial page size
        TenderSearchDTO dto = new TenderSearchDTO();
        dto.setSearchText(searchText.orElse(null));
        dto.setTitle(title.orElse(null));
        dto.setCompanyName(companyName.orElse(null));
        dto.setTenderCategory(tenderCategory.orElse(0));
        dto.setStatus(status.orElse(0));
        dto.setRefNo(refNo.orElse(null));
        dto.setOrderBy(orderBy.orElse(null) == null? TTConstants.DEFAULT_SORT : orderBy.get());
        dto.setAdvance(dto.getSearchText() != null || dto.getTitle() != null || dto.getCompanyName() != null
                        || dto.getTenderCategory() > 0 || dto.getStatus() > 0 || dto.getRefNo() != null);

        int evalPageSize = pageSize.orElse(TTConstants.INITIAL_PAGE_SIZE);
        // Evaluate page. If requested parameter is null or less than 0 (to
        // prevent exception), return initial size. Otherwise, return value of
        // param. decreased by 1.
        int evalPage = (page.orElse(0) < 1) ? TTConstants.INITIAL_PAGE : page.get() - 1;

        Page<Tender> tenders = tenderService.searchTender(dto
                , new PageRequest(
                        evalPage, evalPageSize, getSortPattern(dto)
                ));
        Pager pager = new Pager(tenders.getTotalPages(), tenders.getNumber(), TTConstants.BUTTONS_TO_SHOW);

        model.addAttribute("tenders", tenders);
        model.addAttribute("searchCriteria", dto);
        model.addAttribute("codeValueSvc", codeValueService);
        model.addAttribute("selectedPageSize", evalPageSize);
//        modelAndView.addObject("pageSizes", PAGE_SIZES);
        model.addAttribute("pager", pager);
        if (tenders == null || tenders.getTotalPages() == 0) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.WARNING,
                    "No tenders found.");
            model.addAttribute("alert", alert);
        }
        return "home";
    }

    private Sort getSortPattern(TenderSearchDTO searchDTO) {
        if (searchDTO.getOrderBy().equals(TTConstants.OPEN_DATE)) {
            return new Sort(new Sort.Order(Sort.Direction.ASC, TTConstants.OPEN_DATE));
        } else if(searchDTO.getOrderBy().equals(TTConstants.CLOSED_DATE)) {
            return new Sort(new Sort.Order(Sort.Direction.ASC, TTConstants.CLOSED_DATE));
        } else {
            return new Sort(new Sort.Order(Sort.Direction.ASC, TTConstants.TITLE));
        }
    }
}
