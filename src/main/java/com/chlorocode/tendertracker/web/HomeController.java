package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.Pager;
import com.chlorocode.tendertracker.dao.dto.TenderSearchDTO;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class HomeController {
    private TenderService tenderService;
    private CodeValueService codeValueService;

    @Autowired
    public HomeController(TenderService tenderService, CodeValueService codeValueService) {
        this.tenderService = tenderService;
        this.codeValueService = codeValueService;
    }

//    @GetMapping("/")
//    public String showHomePage(ModelMap model) {
//        model.addAttribute("tenders", tenderService.findTender());
//        model.addAttribute("codeValueSvc", codeValueService);
//        return "home";
//    }

    /**
     * Handles all requests
     *
     * @param pageSize
     * @param page
     * @return model and view
     */
    @GetMapping("/")
    public String showPersonsPage(@RequestParam("pageSize") Optional<Integer> pageSize,
                                        @RequestParam("page") Optional<Integer> page, ModelMap model) {
        //ModelAndView modelAndView = new ModelAndView("home");

        // Evaluate page size. If requested parameter is null, return initial
        // page size
        int evalPageSize = pageSize.orElse(TTConstants.INITIAL_PAGE_SIZE);
        // Evaluate page. If requested parameter is null or less than 0 (to
        // prevent exception), return initial size. Otherwise, return value of
        // param. decreased by 1.
        int evalPage = (page.orElse(0) < 1) ? TTConstants.INITIAL_PAGE : page.get() - 1;

        Page<Tender> tenders = tenderService.listAllByPage(new PageRequest(evalPage, evalPageSize));
        Pager pager = new Pager(tenders.getTotalPages(), tenders.getNumber(), TTConstants.BUTTONS_TO_SHOW);

        model.addAttribute("tenders", tenders);
        model.addAttribute("searchCriteria", new TenderSearchDTO());
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

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }
}
