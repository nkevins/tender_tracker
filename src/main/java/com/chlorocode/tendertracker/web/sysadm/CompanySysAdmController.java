package com.chlorocode.tendertracker.web.sysadm;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDetailsDTO;
import com.chlorocode.tendertracker.dao.dto.ProductUpdateDTO;
import com.chlorocode.tendertracker.dao.dto.TenderClarificationDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.exception.ResourceNotFoundException;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.CompanyService;
import com.chlorocode.tendertracker.service.ProductService;
import com.chlorocode.tendertracker.service.UenEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
public class CompanySysAdmController {

    private CompanyService companyService;
    private UenEntityService uenEntService;
    private ProductService productService;
    private CodeValueService codeValueService;

    @Autowired
    public CompanySysAdmController(CompanyService companyService, UenEntityService uenEntService,ProductService productService,CodeValueService codeValueService) {
        this.companyService = companyService;
        this.uenEntService = uenEntService;
        this.productService = productService;
        this.codeValueService = codeValueService;
    }

    @GetMapping("/sysadm/companyRegistration")
    public String showCompanyRegistration() {
        return "admin/sysadm/companyRegistrationView";
    }

    @GetMapping("/sysadm/companyRegistrationList")
    public String showCompanyRegistrationList() {
        return "admin/sysadm/companyInfoView";
    }

    @GetMapping("/sysadm/tender")
    public String showTenderList() {
        return "admin/sysadm/tenderList";
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

    @GetMapping("/sysadm/product")
    public String showAllProduct() {
        return "admin/sysadm/productList";
    }

    @GetMapping("/sysadmin/product/view/{id}")
    public String showProductPage(@PathVariable(value = "id") Integer id, ModelMap model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/admin/product";
        }

        ProductUpdateDTO productDTO = new ProductUpdateDTO();
        productDTO.setProductCode(product.getProductCode());
        productDTO.setCategory(product.getCategory());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setTitle(product.getTitle());
        productDTO.setType(product.getType());
        productDTO.setPublished(product.isPublish());

        List<CodeValue> lstCode = codeValueService.getByType("product_category");
        for(int i = 0; i < lstCode.size(); i++){
            CodeValue code = lstCode.get(i);
            if(code.getCode() == productDTO.getCategory()){
                productDTO.setCategoryDescription(code.getDescription());
                break;
            }
        }
        model.addAttribute("product", productDTO);
        model.addAttribute("productCategory", codeValueService.getByType("product_category"));

        return "admin/sysadm/productFormView";
    }

    @PostMapping("/sysadmin/product/blacklist")
    public String updateTenderClarificationResponse(@RequestParam("id") int id, ModelMap model){

        Product clari = productService.blacklistProduct(id);
        if(clari == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Failed to blacklist product. Please contact administrator");
            model.addAttribute("alert", alert);
        }else{
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                    "The product is successfully blacklisted");
            model.addAttribute("alert", alert);
        }

        return "admin/sysadm/productList";
    }
}
