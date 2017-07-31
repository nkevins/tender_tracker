package com.chlorocode.tendertracker.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServlet;

@Controller
public class AdminController extends HttpServlet {
    @GetMapping("/admin")
    public String showDashboardPage() {
        return "admin/dashboard";
    }
}
