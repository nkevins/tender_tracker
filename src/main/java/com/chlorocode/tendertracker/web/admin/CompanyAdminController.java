package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.CompanyInfoDTO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Country;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.CompanyService;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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

/**
 * Controller for company profile management page in admin portal.
 */
@Controller
public class CompanyAdminController {

    private CompanyService companyService;
    private CodeValueService codeValueService;

    /**
     * Constructor.
     *
     * @param companyService CompanyService
     * @param codeValueService CodeValueService
     */
    @Autowired
    public CompanyAdminController(CompanyService companyService, CodeValueService codeValueService) {
        this.companyService = companyService;
        this.codeValueService = codeValueService;
    }

    /**
     * This method is used to display company profile page for the current company managed by logged in user.
     *
     * @param model Model Map
     * @return String
     */
    @GetMapping("/admin/company")
    public String showCompanyInfoPage(ModelMap model) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Company company = companyService.findById(usr.getSelectedCompany().getId());

        ModelMapper modelMapper = new ModelMapper();
        PropertyMap<Company, CompanyInfoDTO> companyMap = new PropertyMap<Company, CompanyInfoDTO>() {
            protected void configure() {
                map().setCountry(source.getCountry().getId());
            }
        };
        modelMapper.addMappings(companyMap);
        CompanyInfoDTO companyData = modelMapper.map(company, CompanyInfoDTO.class);

        model.addAttribute("company", companyData);
        model.addAttribute("codeValueSvc", codeValueService);
        model.addAttribute("countries", codeValueService.getAllCountries());
        return "admin/company/companyInfo";
    }

    /**
     * This method is used to update company profile.
     *
     * @param form form data wrapped in CompanyInfoDTO
     * @param model ModelMap
     * @param result to check form binding error
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/admin/company")
    public String updateCompanyInfo(@Valid @ModelAttribute("company") CompanyInfoDTO form, ModelMap model,
                                    BindingResult result, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            model.addAttribute("company", form);
            model.addAttribute("codeValueSvc", codeValueService);
            model.addAttribute("countries", codeValueService.getAllCountries());
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

        Country country = new Country();
        country.setId(form.getCountry());
        company.setCountry(country);

        company.setLastUpdatedBy(usr.getId());
        company.setLastUpdatedDate(new Date());

        try {
            Company updatedCompany = companyService.updateCompany(company);
            usr.setSelectedCompany(updatedCompany);
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, ex.getMessage());
            model.addAttribute("alert", alert);
            model.addAttribute("company", form);
            model.addAttribute("codeValueSvc", codeValueService);
            model.addAttribute("countries", codeValueService.getAllCountries());
            return "admin/company/companyInfo";
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Company Info Updated");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/company";
    }
}
