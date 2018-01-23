package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.dto.*;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.*;
import com.chlorocode.tendertracker.utils.TTCommonUtil;
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
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Controller
public class TenderPublicController {

    private TenderService tenderService;
    private ExternalTenderService externalTenderService;
    private BidService bidService;
    private CodeValueService codeValueService;
    private S3Wrapper s3Wrapper;
    private ClarificationService clariSvc;
    private CorrigendumService corrigendumService;

    @Autowired
    public TenderPublicController(TenderService tenderService, ExternalTenderService externalTenderService
                , BidService bidService, CodeValueService codeValueService, S3Wrapper s3Wrapper
                , ClarificationService clariSvc, CorrigendumService corrigendumService) {
        this.tenderService = tenderService;
        this.externalTenderService = externalTenderService;
        this.bidService = bidService;
        this.codeValueService = codeValueService;
        this.s3Wrapper = s3Wrapper;
        this.clariSvc = clariSvc;
        this.corrigendumService = corrigendumService;
    }

    /**
     * This method use for showing tender details screen.
     *
     * @param id unique identifier of the tender
     * @param model ModelMap
     * @param request HttpServletRequest
     * @return String
     */
    @GetMapping("/tender/{id}")
    public String showTenderDetails(@PathVariable(value="id") Integer id, ModelMap model, HttpServletRequest request) {
        Tender tender = tenderService.findById(id);
        if (tender == null) {
            return "redirect:/";
        }

        // Log visit statistic
        try {
            String remoteAddr;

            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }

            tenderService.logVisit(tender, remoteAddr);
        } catch (Exception e) {
            TTLogger.error(this.getClass().getName(), "Error when logging tender visit", e);
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

            if(tender.getStatus() == 2 || tender.getStatus() == 3){
                isSubmitedTender = true;
            }else
            {
                if(bid == null){
                    isSubmitedTender = false;
                }else{
                    isSubmitedTender = true;
                }
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

    /**
     * This method use for showing tender response screen.
     *
     * @param id unique identifier of the tender
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param model ModelMap
     * @return String
     */
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

    /**
     * This method use to save the tender response.
     * This method will return the name of next screen or null.
     *
     * @param data input data of tender response
     * @param request HttpServletRequest
     * @param resp HttpServletResponse
     * @param model ModelMap
     * @return String
     * @throws IOException
     * @see TenderResponseSubmitDTO
     */
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

    /**
     * This method is use for bookmark tender.
     *
     * @param tenderId unique identifier of tender.
     * @return String
     */
    @PostMapping("/tender/bookmark")
    public String bookmarkTender(@RequestParam("tenderId") int tenderId) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tender tender = tenderService.findById(tenderId);

        tenderService.bookmarkTender(tender, usr.getUser());

        return "redirect:/tender/" + tenderId;
    }

    /**
     * This method use for remove bookmark from tender.
     *
     * @param tenderId unique identifier of tender
     * @return String
     */
    @PostMapping("/tender/removeBookmark")
    public String removeTenderBookmark(@RequestParam("tenderId") int tenderId) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        tenderService.removeTenderBookmark(tenderId, usr.getUser().getId());

        return "redirect:/tender/" + tenderId;
    }

    /**
     * This method use to show subscribe tender category notification screen.
     *
     * @param model ModelMap
     * @return String
     */
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

    /**
     * This method is use to save tender category subscription.
     *
     * @param categories list of categories
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
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

    /**
     * This method use to get TenderSearchDTO.
     *
     * @param request HttpServletRequest
     * @return TenderSearchDTO
     * @see TenderSearchDTO
     */
    @ModelAttribute("searchCriteria")
    public TenderSearchDTO getTenderSearchDTO(HttpServletRequest request)
    {
        return (TenderSearchDTO) request.getAttribute("searchCriteria");
    }

    /**
     * This method use to show valid tenders according to user permission.
     *
     * @param pageSize
     * @param page
     * @return String
     */
    @GetMapping("/tenders")
    public String showTenders(@RequestParam("pageSize") Optional<Integer> pageSize
            , @RequestParam("page") Optional<Integer> page
            , @RequestParam("searchText") Optional<String> searchText
            , @RequestParam("title") Optional<String> title
            , @RequestParam("companyName") Optional<String> companyName
            , @RequestParam("tenderCategory") Optional<Integer> tenderCategory
            , @RequestParam("status") Optional<Integer> status
            , @RequestParam("refNo") Optional<String> refNo
            , @RequestParam("orderBy") Optional<String> orderBy
            , @RequestParam("orderMode") Optional<String> orderMode
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
        dto.setOrderMode(orderMode.orElse(null) == null? TTConstants.DEFAULT_SORT_DIRECTION : orderMode.get());
        dto.setAdvance(dto.getSearchText() != null || dto.getTitle() != null || dto.getCompanyName() != null
                        || dto.getTenderCategory() > 0 || dto.getStatus() > 0 || dto.getRefNo() != null);

        int evalPageSize = pageSize.orElse(TTConstants.INITIAL_PAGE_SIZE);
        // Evaluate page. If requested parameter is null or less than 0 (to
        // prevent exception), return initial size. Otherwise, return value of
        // param. decreased by 1.
        int evalPage = (page.orElse(0) < 1) ? TTConstants.INITIAL_PAGE : page.get() - 1;

        Page<Tender> tenders = tenderService.searchTender(dto
                , new PageRequest(
                        evalPage, evalPageSize, getSortPattern(dto, false)
                ));
        Pager pager = new Pager(tenders.getTotalPages(), tenders.getNumber(), TTConstants.BUTTONS_TO_SHOW);

        model.addAttribute("tenders", tenders);
        model.addAttribute("searchCriteria", dto);
        model.addAttribute("codeValueSvc", codeValueService);
        model.addAttribute("selectedPageSize", evalPageSize);
        model.addAttribute("pager", pager);
        if (tenders == null || tenders.getTotalPages() == 0) {
            model.addAttribute("noTenderFound", true);
        } else {
            model.addAttribute("noTenderFound", false);
        }
        return "home";
    }

    /**
     * This method use to show all external tenders.
     *
     * @param pageSize
     * @param page
     * @return String
     */
    @GetMapping("/external_tenders")
    public String showExternalTenders(@RequestParam("pageSize") Optional<Integer> pageSize
            , @RequestParam("page") Optional<Integer> page
            , @RequestParam("searchText") Optional<String> searchText
            , @RequestParam("title") Optional<String> title
            , @RequestParam("companyName") Optional<String> companyName
            , @RequestParam("status") Optional<Integer> status
            , @RequestParam("tenderSource") Optional<Integer> tenderSource
            , @RequestParam("refNo") Optional<String> refNo
            , @RequestParam("orderBy") Optional<String> orderBy
            , @RequestParam("orderMode") Optional<String> orderMode
            , ModelMap model) {
        // Evaluate page size. If requested parameter is null, return initial page size
        TenderSearchDTO dto = new TenderSearchDTO();
        dto.setSearchText(searchText.orElse(null));
        dto.setTitle(title.orElse(null));
        dto.setCompanyName(companyName.orElse(null));
        if (status.orElse(0) > 0) {
            dto.setEtStatus(TTCommonUtil.getExternalTenderStatus(status.orElse(0)));
        }
        dto.setTenderSource(tenderSource.orElse(0));
        dto.setRefNo(refNo.orElse(null));
        dto.setOrderBy(orderBy.orElse(null) == null? TTConstants.DEFAULT_SORT : orderBy.get());
        dto.setOrderMode(orderMode.orElse(null) == null? TTConstants.DEFAULT_SORT_DIRECTION : orderMode.get());
        dto.setAdvance(dto.getSearchText() != null || dto.getTitle() != null || dto.getCompanyName() != null
                || dto.getEtStatus() != null || dto.getTenderSource() > 0 || dto.getRefNo() != null);

        int evalPageSize = pageSize.orElse(TTConstants.INITIAL_PAGE_SIZE);
        // Evaluate page. If requested parameter is null or less than 0 (to
        // prevent exception), return initial size. Otherwise, return value of
        // param. decreased by 1.
        int evalPage = (page.orElse(0) < 1) ? TTConstants.INITIAL_PAGE : page.get() - 1;

        Page<ExternalTender> externalTenders = externalTenderService.searchTender(dto
                , new PageRequest(
                        evalPage, evalPageSize, getSortPattern(dto, true)
                ));
        Pager pager = new Pager(externalTenders.getTotalPages(), externalTenders.getNumber(), TTConstants.BUTTONS_TO_SHOW);

        model.addAttribute("external_tenders", externalTenders);
        model.addAttribute("searchCriteria", dto);
        model.addAttribute("codeValueSvc", codeValueService);
        model.addAttribute("selectedPageSize", evalPageSize);
        model.addAttribute("pager", pager);
        if (externalTenders == null || externalTenders.getTotalPages() == 0) {
            model.addAttribute("noTenderFound", true);
        } else {
            model.addAttribute("noTenderFound", false);
        }
        return "external_tenders";
    }

    /**
     * This method use to redirect to GeBiz.
     *
     * @param id unique identifier of tender
     * @return String
     */
    @GetMapping("/external_tenders/GeBiz/{id}")
    public String redirectToGeBiz(@PathVariable(value = "id") Integer id) {
        ExternalTender externalTender = externalTenderService.findByID(id);
        String content = null;
        URLConnection connection = null;
        try {
            connection =  new URL("https://www.gebiz.gov.sg/ptn/opportunity/opportunityDetails.xhtml?code=" + externalTender.getReferenceNo()).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            if (content.contains("No opportunity found for your search") || content.contains("302")) {
                return "redirect:https://www.gebiz.gov.sg/ptn/opportunityportal/opportunityDetails.xhtml?code=" + externalTender.getReferenceNo();
            } else {
                return "redirect:https://www.gebiz.gov.sg/ptn/opportunity/opportunityDetails.xhtml?code=" + externalTender.getReferenceNo();
            }
        }catch ( Exception ex ) {
            TTLogger.error(this.getClass().getName(),"Error when redirecting to GeBiz", ex);
        }

        return "redirect:" + externalTender.getTenderURL();
    }

    /**
     * This method use to get sort pattern of the tenders screen and external tender screen.
     *
     * @param searchDTO TenderSearchDTO
     * @param isExternal to check external tender or not
     * @return Sort
     */
    private Sort getSortPattern(TenderSearchDTO searchDTO, boolean isExternal) {
        Sort.Direction direction = Sort.Direction.ASC;
        // Set order direction.
        if (searchDTO.getOrderMode().equals(TTConstants.DESC)) {
            direction = Sort.Direction.DESC;
        }

        // Set order by attribute.
        if (searchDTO.getOrderBy().equals(TTConstants.OPEN_DATE)) {
            if (isExternal) {
                return new Sort(new Sort.Order(direction, TTConstants.PUBLISHED_DATE));
            } else {
                return new Sort(new Sort.Order(direction, TTConstants.OPEN_DATE));
            }
        } else if(searchDTO.getOrderBy().equals(TTConstants.CLOSED_DATE)) {
            if (isExternal) {
                return new Sort(new Sort.Order(direction, TTConstants.CLOSING_DATE));
            } else {
                return new Sort(new Sort.Order(direction, TTConstants.CLOSED_DATE));
            }
        } else {
            return new Sort(new Sort.Order(direction, TTConstants.TITLE));
        }
    }
}
