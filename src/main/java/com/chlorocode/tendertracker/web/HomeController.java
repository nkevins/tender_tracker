package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private TenderService tenderService;
    private CodeValueService codeValueService;

    @Autowired
    public HomeController(TenderService tenderService, CodeValueService codeValueService) {
        this.tenderService = tenderService;
        this.codeValueService = codeValueService;
    }

    @GetMapping("/")
    public String showHomePage(ModelMap model) {
        model.addAttribute("tenders", tenderService.findTender());
        model.addAttribute("codeValueSvc", codeValueService);
        return "home";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }
}
