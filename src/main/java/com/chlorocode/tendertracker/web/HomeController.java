package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.Pager;
import com.chlorocode.tendertracker.dao.dto.TenderSearchDTO;
import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.ExternalTenderService;
import com.chlorocode.tendertracker.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Iterator;
import java.util.Optional;

/**
 * Controller class for user home screen.
 */
@Controller
public class HomeController {
    private TenderService tenderService;
    private ExternalTenderService externalTenderService;
    private CodeValueService codeValueService;

    @Autowired
    public HomeController(TenderService tenderService, CodeValueService codeValueService, ExternalTenderService externalTenderService) {
        this.tenderService = tenderService;
        this.codeValueService = codeValueService;
        this.externalTenderService = externalTenderService;
    }

    /**
     * Handles all requests
     *
     * @param pageSize
     * @param page
     * @param model
     * @return String
     */
    @GetMapping("/")
    public String showPersonsPage(@RequestParam("pageSize") Optional<Integer> pageSize,
                                        @RequestParam("page") Optional<Integer> page, ModelMap model) {
        // Evaluate page size. If requested parameter is null, return initial page size
        int evalPageSize = pageSize.orElse(TTConstants.INITIAL_PAGE_SIZE);
        /*
            Evaluate page. If requested parameter is null or less than 0 (to
         prevent exception), return initial size. Otherwise, return value of
         param. decreased by 1.
         */
        int evalPage = (page.orElse(0) < 1) ? TTConstants.INITIAL_PAGE : page.get() - 1;

        Page<Tender> tenders = tenderService.listAllByPage(
                new PageRequest(
                    evalPage, evalPageSize, new Sort(new Sort.Order(Sort.Direction.DESC, TTConstants.OPEN_DATE))
                ));
        Pager pager = new Pager(tenders.getTotalPages(), tenders.getNumber(), TTConstants.BUTTONS_TO_SHOW);

        TenderSearchDTO tenderSearchDTO = new TenderSearchDTO();
        tenderSearchDTO.setOrderBy(TTConstants.OPEN_DATE);
        tenderSearchDTO.setOrderMode(TTConstants.DESC);
        model.addAttribute("tenders", tenders);
        model.addAttribute("searchCriteria", tenderSearchDTO);
        model.addAttribute("codeValueSvc", codeValueService);
        model.addAttribute("selectedPageSize", evalPageSize);
        model.addAttribute("pager", pager);
        if (tenders == null || tenders.getTotalPages() == 0) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.WARNING,
                    "No tenders found.");
            model.addAttribute("alert", alert);
        }
        return "home";
    }

    /**
     * This method is used to show eligible external tender.
     *
     * @param pageSize
     * @param page
     * @param model
     * @return String
     */
    @GetMapping("/external_tender")
    public String showExternalTender(@RequestParam("pageSize") Optional<Integer> pageSize,
                                  @RequestParam("page") Optional<Integer> page, ModelMap model) {
        // Evaluate page size. If requested parameter is null, return initial page size
        int evalPageSize = pageSize.orElse(TTConstants.INITIAL_PAGE_SIZE);

        /*
            Evaluate page. If requested parameter is null or less than 0 (to
         prevent exception), return initial size. Otherwise, return value of
         param. decreased by 1.
         */
        int evalPage = (page.orElse(0) < 1) ? TTConstants.INITIAL_PAGE : page.get() - 1;

        Page<ExternalTender> externalTenders = externalTenderService.listAllByPage(
                new PageRequest(
                        evalPage, evalPageSize, new Sort(new Sort.Order(Sort.Direction.DESC, TTConstants.PUBLISHED_DATE))
                ));
        Pager pager = new Pager(externalTenders.getTotalPages(), externalTenders.getNumber(), TTConstants.BUTTONS_TO_SHOW);

        for (ExternalTender exTender : externalTenders.getContent()) {
            TTLogger.debug(HomeController.class.getName(), "****ExternalTender="+exTender.toString());
        }

        TenderSearchDTO tenderSearchDTO = new TenderSearchDTO();
        tenderSearchDTO.setOrderBy(TTConstants.OPEN_DATE);
        tenderSearchDTO.setOrderMode(TTConstants.DESC);
        model.addAttribute("external_tenders", externalTenders);
        model.addAttribute("searchCriteria", tenderSearchDTO);
        model.addAttribute("codeValueSvc", codeValueService);
        model.addAttribute("selectedPageSize", evalPageSize);
        model.addAttribute("pager", pager);
        if (externalTenders == null || externalTenders.getTotalPages() == 0) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.WARNING,
                    "No tenders found.");
            model.addAttribute("alert", alert);
        }
        return "external_tenders";
    }

    /**
     * This method is used to show about page of the website.
     *
     * @return String
     */
    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }
}
