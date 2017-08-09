package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.CompanyInfoDTO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.CompanyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Date;

@Controller
public class CompanyAdminController {

    private CompanyService companyService;
    private CodeValueService codeValueService;

    @Autowired
    public CompanyAdminController(CompanyService companyService, CodeValueService codeValueService) {
        this.companyService = companyService;
        this.codeValueService = codeValueService;
    }

    @GetMapping("/admin/company")
    public String showCompanyInfoPage(ModelMap model) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Company company = usr.getSelectedCompany();

        ModelMapper modelMapper = new ModelMapper();
        CompanyInfoDTO companyData = modelMapper.map(company, CompanyInfoDTO.class);

        model.addAttribute("company", companyData);
        model.addAttribute("codeValueSvc", codeValueService);
        return "admin/company/companyInfo";
    }

    @PostMapping("/admin/company")
    public String updateCompanyInfo(@Valid @ModelAttribute("company") CompanyInfoDTO form, ModelMap model,
                                    BindingResult result, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            model.addAttribute("company", form);
            model.addAttribute("codeValueSvc", codeValueService);
            return "admin/company/companyInfo";
        }

        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Company company = companyService.findById(form.getId());
        company.setAddress1(form.getAddress1());
        company.setAddress2(form.getAddress2());
        company.setPostalCode(form.getPostalCode());
        company.setCity(form.getCity());
        company.setState(form.getState());
        company.setProvince(form.getProvince());
        company.setCountry(form.getCountry());
        company.setLastUpdatedBy(usr.getId());
        company.setLastUpdatedDate(new Date());

        companyService.updateCompany(company);

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Company Info Updated");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/company";
    }
}
