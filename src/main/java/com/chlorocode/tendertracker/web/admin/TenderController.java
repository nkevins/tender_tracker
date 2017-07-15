package com.chlorocode.tendertracker.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TenderController {

    @GetMapping("/admin/tender")
    public String showTenderPage() {
        return "admin/tender";
    }
}
