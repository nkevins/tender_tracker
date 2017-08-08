package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.service.ClarificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by andy on 8/8/2017.
 */
@Controller
public class TenderClarificationController {
    private ClarificationService clariSvc;

    @Autowired
    public TenderClarificationController(ClarificationService clariSvc){
        this.clariSvc = clariSvc;
    }

    @GetMapping("/admin/tender/clarification")
    public String showTenderClarificationPage() {
        return "admin/clarification/tenderClarificationList";
    }
}
