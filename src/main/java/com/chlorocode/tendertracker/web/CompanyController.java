package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDTO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CompanyRegistration;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class CompanyController {

    private CodeValueService codeValueService;
    private CompanyService companyService;

    @Autowired
    public CompanyController(CodeValueService codeValueService, CompanyService companyService) {
        this.codeValueService = codeValueService;
        this.companyService = companyService;
    }

    @GetMapping("registerCompany")
    public String showRegisterCompany(ModelMap model) {
        model.addAttribute("registration", new CompanyRegistrationDTO());
        model.addAttribute("areaOfBusiness", codeValueService.getByType("area_of_business"));
        model.addAttribute("companyType", codeValueService.getByType("company_type"));
        return "registerCompany";
    }

    @PostMapping("registerCompany")
    public String saveCompanyRegistration(@Valid @ModelAttribute("registration") CompanyRegistrationDTO form,
                                          BindingResult result, RedirectAttributes redirectAttrs, ModelMap model) {
        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            return "registerCompany";
        }

        Company reg = new Company();
        reg.setName(form.getName());
        reg.setUen(form.getUen());
        reg.setGstRegNo(form.getGstRegNo());
        reg.setType(form.getType());
        reg.setAddress1(form.getAddress1());
        reg.setAddress2(form.getAddress2());
        reg.setPostalCode(form.getPostalCode());
        reg.setCity(form.getCity());
        reg.setState(form.getState());
        reg.setProvince(form.getProvince());
        reg.setCountry(form.getCountry());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser usr = (CurrentUser) auth.getPrincipal();
        //reg.setApplicant(usr.getUser());
        reg.setCreatedBy(usr.getUser().getId());
        reg.setLastUpdatedBy(usr.getUser().getId());

        companyService.registerCompany(reg);


        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "Company Registered Successfuly");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/";
    }
}
