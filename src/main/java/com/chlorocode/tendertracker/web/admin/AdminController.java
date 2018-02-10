package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Controller for admin dashboard page.
 */
@Controller
public class AdminController extends HttpServlet {

    private CompanyService companyService;

    /**
     * Constructor
     *
     * @param companyService CompanyService
     */
    @Autowired
    public AdminController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * This method is used for showing admin dashboard page.
     *
     * @param modelMap ModelMap
     * @param request HttpServletRequest
     * @return String
     */
    @GetMapping("/admin")
    public String showDashboardPage(ModelMap modelMap, HttpServletRequest request) {
        if (request.isUserInRole("ROLE_SYS_ADMIN")) {
            modelMap.addAttribute("companyPendingApprovalCount", companyService.findCompanyPendingApproval().size());
        }

        return "admin/dashboard";
    }
}
