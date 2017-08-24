package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDTO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Country;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.CompanyService;
import com.chlorocode.tendertracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
public class CompanyController {

    private CodeValueService codeValueService;
    private CompanyService companyService;
    private UserService userService;

    @Autowired
    public CompanyController(CodeValueService codeValueService, CompanyService companyService, UserService userService) {
        this.codeValueService = codeValueService;
        this.companyService = companyService;
        this.userService = userService;
    }

    @GetMapping("registerCompany")
    public String showRegisterCompany(ModelMap model) {
        CompanyRegistrationDTO dto = new CompanyRegistrationDTO();
        dto.setCountry("SG");

        model.addAttribute("registration", dto);
        model.addAttribute("areaOfBusiness", codeValueService.getByType("area_of_business"));
        model.addAttribute("companyType", codeValueService.getByType("company_type"));
        model.addAttribute("countries", codeValueService.getAllCountries());
        return "registerCompany";
    }

    @PostMapping("registerCompany")
    public String saveCompanyRegistration(@Valid @ModelAttribute("registration") CompanyRegistrationDTO form,
                                          BindingResult result, RedirectAttributes redirectAttrs, ModelMap model) {
        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            model.addAttribute("registration", form);
            model.addAttribute("areaOfBusiness", codeValueService.getByType("area_of_business"));
            model.addAttribute("companyType", codeValueService.getByType("company_type"));
            model.addAttribute("countries", codeValueService.getAllCountries());
            return "registerCompany";
        }

        List<Company> compList = companyService.findCompanyByUen(form.getUen());

        if(compList != null && compList.size() > 0){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "This company UEN already exist");
            model.addAttribute("alert", alert);
            model.addAttribute("registration", form);
            model.addAttribute("areaOfBusiness", codeValueService.getByType("area_of_business"));
            model.addAttribute("companyType", codeValueService.getByType("company_type"));
            model.addAttribute("countries", codeValueService.getAllCountries());
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

        Country country = new Country();
        country.setId(form.getCountry());
        reg.setCountry(country);

        reg.setAreaOfBusiness(form.getAreaOfBusiness());

        reg.setPrincpleBusinessActivity(form.getPrincipleBusinessActivity());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser usr = (CurrentUser) auth.getPrincipal();
        reg.setCreatedBy(usr.getUser().getId());
        reg.setLastUpdatedBy(usr.getUser().getId());

        try {
            companyService.registerCompany(reg);
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, ex.getMessage());
            model.addAttribute("alert", alert);
            model.addAttribute("registration", form);
            model.addAttribute("areaOfBusiness", codeValueService.getByType("area_of_business"));
            model.addAttribute("companyType", codeValueService.getByType("company_type"));
            model.addAttribute("countries", codeValueService.getAllCountries());
            return "registerCompany";
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "Company Registered Successfuly");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/";
    }

    @GetMapping("/company/{id}")
    public String showCompanyDetail(@PathVariable(value = "id") Integer id, ModelMap model) {
        Company company = companyService.findById(id);
        if (company == null || company.getStatus() != 1) {
            return "redirect:/";
        }

        model.addAttribute("company", company);
        model.addAttribute("areaOfBusiness", codeValueService.getDescription("area_of_business", company.getAreaOfBusiness()));
        model.addAttribute("contact", userService.findById(company.getCreatedBy()));

        return "companyDetails";
    }
}
