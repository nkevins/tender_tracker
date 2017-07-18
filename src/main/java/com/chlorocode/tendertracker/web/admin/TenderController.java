package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.TenderCreateDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class TenderController {

    private CodeValueService codeValueService;
    private TenderService tenderService;

    @Autowired
    public TenderController(CodeValueService codeValueService, TenderService tenderService) {
        this.codeValueService = codeValueService;
        this.tenderService = tenderService;
    }

    @GetMapping("/admin/tender")
    public String showTenderPage() {
        return "admin/tender/tenderView";
    }

    @GetMapping("/admin/tender/create")
    public String showCreateTenderPage(ModelMap model) {
        model.addAttribute("tender", new TenderCreateDTO());
        model.addAttribute("tenderType", codeValueService.getByType("tender_type"));
        model.addAttribute("tenderCategories", codeValueService.getAllTenderCategories());
        return "admin/tender/tenderCreate";
    }

    @PostMapping("/admin/tender/create")
    public String saveCreateTender(@Valid @ModelAttribute("tender") TenderCreateDTO form, BindingResult result,
                                   RedirectAttributes redirectAttrs, ModelMap model) {
        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            model.addAttribute("tender", form);
            model.addAttribute("tenderType", codeValueService.getByType("tender_type"));
            model.addAttribute("tenderCategories", codeValueService.getAllTenderCategories());
            return "admin/tender/tenderCreate";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser usr = (CurrentUser) auth.getPrincipal();

        Tender t = new Tender();
        t.setRefNo(form.getRefNo());
        t.setTitle(form.getTitle());
        t.setOpenDate(form.getOpenDate());
        t.setClosedDate(form.getClosedDate());
        t.setCompany(usr.getSelectedCompany());
        t.setTenderCategory(codeValueService.getTenderCategoryById(form.getTenderCategory()));
        t.setDescription(form.getDescription());
        t.setTenderType(form.getTenderType());
        t.setEstimatePurchaseValue(form.getEstimatePurchaseValue());
        t.setActualPurchaseValue(0);
        t.setDeliveryDate(form.getDeliveryDate());
        t.setDeliveryLocation(form.getDeliveryLocation());
        t.setDeliveryRemarks(form.getDeliveryRemarks());
        t.setContactPersonName(form.getContactPersonName());
        t.setContactPersonEmail(form.getContactPersonEmail());
        t.setContactPersonPhone(form.getContactPersonPhone());
        t.setStatus(1);
        t.setCreatedBy(usr.getId());
        t.setCreatedDate(new Date());
        t.setLastUpdatedBy(usr.getId());
        t.setLastUpdatedDate(new Date());

        try {
            tenderService.createTender(t);

            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                    "Tender Created");
            redirectAttrs.addFlashAttribute("alert", alert);
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            model.addAttribute("alert", alert);
            model.addAttribute("tender", form);
            model.addAttribute("tenderType", codeValueService.getByType("tender_type"));
            model.addAttribute("tenderCategories", codeValueService.getAllTenderCategories());

            return "admin/tender/tenderCreate";
        }

        return "redirect:/admin/tender";
    }

    @InitBinder
    public void binder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch(ParseException e) {
                    setValue(null);
                }
            }

            public String getAsText() {
                if (getValue() == null) {
                    return "";
                } else {
                    return new SimpleDateFormat("dd/MM/yyyy").format((Date) getValue());
                }
            }
        });
    }
}
