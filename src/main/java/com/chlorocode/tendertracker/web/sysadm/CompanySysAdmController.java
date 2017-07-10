package com.chlorocode.tendertracker.web.sysadm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CompanySysAdmController {

    @GetMapping("/sysadm/companyRegistration")
    public String showCompanyRegistration() {

        return "admin/sysadm/companyRegistrationView";
    }
}
