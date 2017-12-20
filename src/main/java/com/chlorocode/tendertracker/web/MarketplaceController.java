package com.chlorocode.tendertracker.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MarketplaceController {

    @GetMapping("/marketplace")
    public String marketplace() {
        return "marketplace";
    }
}
