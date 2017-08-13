package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

@Controller
public class AdminController extends HttpServlet {

    CompanyService companyService;

    @Autowired
    public AdminController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/admin")
    public String showDashboardPage(ModelMap modelMap, HttpServletRequest request) {
        if (request.isUserInRole("ROLE_SYS_ADMIN")) {
            modelMap.addAttribute("companyPendingApprovalCount", companyService.findCompanyPendingApproval().size());
        }

        return "admin/dashboard";
    }
}
