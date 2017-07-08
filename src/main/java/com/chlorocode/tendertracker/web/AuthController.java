package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.LoginDTO;
import com.chlorocode.tendertracker.dao.dto.UserRegistrationDTO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.CompanyService;
import com.chlorocode.tendertracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {

    private UserService userService;
    private CompanyService companyService;

    @Autowired
    public AuthController(UserService userService, CompanyService companyService) {
        this.userService = userService;
        this.companyService = companyService;
    }

    @RequestMapping("/login")
    public String showLogin(ServletRequest request, Model model) {
        Map<String, String[]> paramMap = request.getParameterMap();

        if (paramMap.containsKey("error"))
        {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Invalid username or password");
            model.addAttribute("alert", alert);
        }
        model.addAttribute("login", new LoginDTO());

        return "login";
    }

    @GetMapping("/register")
    public ModelAndView showUserRegistration() {
        return new ModelAndView("registerUser", "registration", new UserRegistrationDTO());
    }

    @PostMapping("/register")
    public String saveUserRegistration(@Valid @ModelAttribute("registration") UserRegistrationDTO form,
                                       BindingResult result, RedirectAttributes redirectAttrs, ModelMap model) {

        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            return "registerUser";
        } else if (!form.getPassword().equals(form.getConfirmPassword())) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Password Confirmation is not same as the password");
            model.addAttribute("alert", alert);
            return "registerUser";
        }

        User user = new User();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setContactNo(form.getContactNo());
        user.setPassword(form.getPassword());

        try {
            userService.create(user);
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, ex.getMessage());
            model.addAttribute("alert", alert);
            return "registerUser";
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "Registration Successful");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/login";
    }

    @GetMapping("/selectCompany")
    public ModelAndView showSelectCompany() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser usr = (CurrentUser) auth.getPrincipal();
        List<Company> administeredCompanies = usr.getCompanyAdministered();

        return new ModelAndView("selectCompany", "company", administeredCompanies);
    }

    @PostMapping("/selectCompany")
    public String selectCompanyToManage(@RequestParam("companyId") String companyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser usr = (CurrentUser) auth.getPrincipal();

        usr.setSelectedCompany(companyService.findById(Integer.parseInt(companyId)));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(usr, null, usr.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return "redirect:/admin";
    }
}
