package com.chlorocode.tendertracker.web.sysadm;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDetailsDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.UenEntity;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.exception.ResourceNotFoundException;
import com.chlorocode.tendertracker.service.CompanyService;
import com.chlorocode.tendertracker.service.UenEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CompanySysAdmController {

    private CompanyService companyService;
    private UenEntityService uenEntService;

    @Autowired
    public CompanySysAdmController(CompanyService companyService, UenEntityService uenEntService) {
        this.companyService = companyService;
        this.uenEntService = uenEntService;
    }

    @GetMapping("/sysadm/companyRegistration")
    public String showCompanyRegistration() {
        return "admin/sysadm/companyRegistrationView";
    }

    @GetMapping("/sysadm/companyRegistrationList")
    public String showCompanyRegistrationList() {
        return "admin/sysadm/companyInfoView";
    }

    @GetMapping("/sysadm/companyInfo/{id}")
    public String showcompanyInfo(@PathVariable(value="id") Integer id, Model model) {
        CompanyRegistrationDetailsDTO companyRegistration = companyService.findCompanyRegistrationById(id);

        if (companyRegistration == null) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("reg", companyRegistration);

        UenEntity uenEnt = uenEntService.findByUen(companyRegistration.getUen());
        if(uenEnt == null){
            model.addAttribute("uenInvalidLabel","Unverified UEN");
        }else{
            model.addAttribute("uenValidLabel", "UEN Verified");
        }

        if(companyRegistration.getStatus().equalsIgnoreCase("Approved")){
            model.addAttribute("approved","Approved");
        }else if(companyRegistration.getStatus().equalsIgnoreCase("Rejected")){
            model.addAttribute("rejected","Rejected");
        }

        if(companyRegistration.isActive()){
            model.addAttribute("active","Active");
        }else if(!companyRegistration.isActive()){
            model.addAttribute("blacklisted","Blacklisted");
        }
        return "admin/sysadm/companyDetail";
    }

    @GetMapping("/sysadm/companyRegistration/{id}")
    public String showCompanyRegistrationDetail(@PathVariable(value="id") Integer id, Model model) {
        CompanyRegistrationDetailsDTO companyRegistration = companyService.findCompanyRegistrationById(id);

        if (companyRegistration == null) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("reg", companyRegistration);

        UenEntity uenEnt = uenEntService.findByUen(companyRegistration.getUen());
        if(uenEnt == null){
            model.addAttribute("uenInvalidLabel","Unverified UEN");
        }else{
            model.addAttribute("uenValidLabel", "UEN Verified");
        }

        return "admin/sysadm/companyRegistrationDetail";
    }

    @PostMapping("/sysadm/blacklistCompany")
    public String blacklistCompany(@RequestParam("id") int id, @RequestParam("action") String action,
                                                   RedirectAttributes redirectAttrs) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try{
            if (action.equals("reject")) {
                boolean result = companyService.blacklistCompany(id,  usr.getId());
                if(result){
                    AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                            "Company blacklisted Successful");
                    redirectAttrs.addFlashAttribute("alert", alert);
                }else{
                    AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                            "Something went wrong. Cannot blacklisted the company");
                    redirectAttrs.addFlashAttribute("alert", alert);
                }
            }else{
                boolean result = companyService.unblacklistCompany(id,  usr.getId());
                if(result){
                    AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                            "Company unblacklisted Successful");
                    redirectAttrs.addFlashAttribute("alert", alert);
                }else{
                    AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                            "Something went wrong. Cannot blacklisted the company");
                    redirectAttrs.addFlashAttribute("alert", alert);
                }
            }


        }catch (ApplicationException ex){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error encountered: " + ex.getMessage());
            redirectAttrs.addFlashAttribute("alert", alert);
        }


        return "redirect:/sysadm/companyRegistrationList";
    }

    @PostMapping("/sysadm/companyRegistration")
    public String approveRejectCompanyRegistration(@RequestParam("id") int id, @RequestParam("action") String action,
                                                   RedirectAttributes redirectAttrs) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            if (action.equals("approve")) {
                companyService.approveCompanyRegistration(id, usr.getId());

                AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                        "Approval Successful");
                redirectAttrs.addFlashAttribute("alert", alert);
            } else if (action.equals("reject")) {
                companyService.rejectCompanyRegistration(id, usr.getId());

                AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                        "Rejection Successful");
                redirectAttrs.addFlashAttribute("alert", alert);
            }
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error encountered: " + ex.getMessage());
            redirectAttrs.addFlashAttribute("alert", alert);
        }

        return "redirect:/sysadm/companyRegistration";
    }
}
