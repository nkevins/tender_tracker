package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.*;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller for the tender page in admin portal.
 */
@Controller
public class TenderController {

    private CodeValueService codeValueService;
    private TenderService tenderService;
    private S3Wrapper s3Service;
    private UserService userService;
    private CompanyService companyService;
    private TenderItemService tenderItemService;

    /**
     * Constructor.
     *
     * @param codeValueService CodeValueService
     * @param tenderService TenderService
     * @param s3Service S3Wrapper
     * @param userService UserService
     * @param companyService CompanyService
     * @param tenderItemService TenderItemService
     */
    @Autowired
    public TenderController(CodeValueService codeValueService, TenderService tenderService, S3Wrapper s3Service,
                            UserService userService, CompanyService companyService, TenderItemService tenderItemService) {
        this.codeValueService = codeValueService;
        this.tenderService = tenderService;
        this.s3Service = s3Service;
        this.userService = userService;
        this.companyService = companyService;
        this.tenderItemService = tenderItemService;
    }

    /**
     * This method is used to display page to list all tenders of the company.
     *
     * @return String
     */
    @GetMapping("/admin/tender")
    public String showTenderPage() {
        return "admin/tender/tenderView";
    }

    /**
     * This method is used to display page containing list of tenders to be evaluated.
     *
     * @return String
     */
    @GetMapping("/admin/tender/evaluation")
    public String showTenderEvaluationPage() {
        return "admin/tender/tenderEvaluationView";
    }

    /**
     * This method is used to display create tender page.
     *
     * @param model ModelMap
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @GetMapping("/admin/tender/create")
    public String showCreateTenderPage(ModelMap model, RedirectAttributes redirectAttrs) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser usr = (CurrentUser) auth.getPrincipal();
        Company comp = usr.getSelectedCompany();
        Company selectedComp = companyService.findById(comp.getId());
        if(!selectedComp.isActive()){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "This company had been blacklisted by Administrator. Kindly please contact Administrator for more details");
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/tender";
        }

        model.addAttribute("tender", new TenderCreateDTO());
        model.addAttribute("tenderType", codeValueService.getByType("tender_type"));
        model.addAttribute("tenderCategories", codeValueService.getAllTenderCategories());
        model.addAttribute("uom", codeValueService.getByType("uom"));
        return "admin/tender/tenderCreate";
    }

    /**
     * This method is used to create a tender.
     * This method can handle request both from normal HTTP and AJAX.
     * This method will return the name of next screen or null for AJAX response.
     * For AJAX response, this method will return the HTTP status and any error in the HTTP body.
     *
     * @param form input data from user
     * @param result binding result to check DTO validation result
     * @param redirectAttrs RedirectAttributes
     * @param model ModelMap
     * @param request HttpServletRequest
     * @param resp HttpServletResponse
     * @return String
     * @throws IOException if has exception when putting error message in AJAX response
     * @see TenderCreateDTO
     */
    @PostMapping("/admin/tender/create")
    public String saveCreateTender(@Valid @ModelAttribute("tender") TenderCreateDTO form, BindingResult result,
                                   RedirectAttributes redirectAttrs, ModelMap model, HttpServletRequest request,
                                   HttpServletResponse resp) throws IOException {
        String requestedWith = request.getHeader("X-Requested-With");
        Boolean isAjax = requestedWith != null && "XMLHttpRequest".equals(requestedWith);

        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            model.addAttribute("tender", form);
            model.addAttribute("tenderType", codeValueService.getByType("tender_type"));
            model.addAttribute("tenderCategories", codeValueService.getAllTenderCategories());
            model.addAttribute("uom", codeValueService.getByType("uom"));

            if (isAjax) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print(result.getAllErrors().get(0).getDefaultMessage());
                return null;
            } else {
                return "admin/tender/tenderCreate";
            }
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
        t.setCreatedBy(usr.getId());
        t.setCreatedDate(new Date());
        t.setLastUpdatedBy(usr.getId());
        t.setLastUpdatedDate(new Date());

        if (form.getItems() != null) {
            for (TenderItemCreateDTO i : form.getItems()) {
                TenderItem item = new TenderItem();
                item.setUom(i.getUom());
                item.setQuantity(i.getQuantity());
                item.setDescription(i.getDescription());
                item.setCreatedBy(usr.getId());
                item.setCreatedDate(new Date());
                item.setLastUpdatedBy(usr.getId());
                item.setLastUpdatedDate(new Date());

                t.addTenderItem(item);
            }
        }

        try {
            if (t.getTenderType() == 2) {
                if (form.getInvitedCompany() == null || form.getInvitedCompany().trim().equals("")) {
                    throw new ApplicationException("For Closed Tender, please provide at least one company to be invited");
                }

                String[] companyId = form.getInvitedCompany().split(",");
                for (String c : companyId) {
                    Company company = companyService.findById(Integer.parseInt(c));
                    t.addInvitedCompany(company);
                }
            }

            tenderService.createTender(t, form.getAttachments());

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
            model.addAttribute("uom", codeValueService.getByType("uom"));

            if (isAjax) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print(ex.getMessage());
                return null;
            } else {
                return "admin/tender/tenderCreate";
            }
        }

        if (isAjax) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return null;
        } else {
            return "redirect:/admin/tender";
        }
    }

    @InitBinder
    public void binder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(value));
                } catch(ParseException e) {
                    try {
                        setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                    } catch(ParseException e2) {
                        setValue(null);
                    }
                }
            }

            public String getAsText() {
                if (getValue() == null) {
                    return "";
                } else {
                    return new SimpleDateFormat("dd/MM/yyyy HH:mm").format((Date) getValue());
                }
            }
        });
    }

    /**
     * This method is used to display page containing tender details.
     *
     * @param id unique identifier of the tender
     * @param model ModelMap
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @GetMapping("/admin/tender/{id}")
    public String showTenderDetails(@PathVariable(value="id") Integer id, ModelMap model, RedirectAttributes redirectAttrs) {
        Tender tender = tenderService.findById(id);
        if (tender == null) {
            return "redirect:/admin/tender";
        }

        //perform validation check, if the tender is not created by this company, stop to view it
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(tender.getCompany().getId() != usr.getSelectedCompany().getId()){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "You are not auhtorized to view the tender details");
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/tender";
        }

        User user = userService.findById(tender.getCreatedBy());

        model.addAttribute("tender", tender);
        model.addAttribute("tenderType", codeValueService.getDescription("tender_type", tender.getTenderType()));
        model.addAttribute("codeValueService", codeValueService);
        model.addAttribute("s3Service", s3Service);
        model.addAttribute("createdBy", user.getName());
        return "admin/tender/tenderDetails";
    }

    /**
     * This method is used to display update tender page.
     *
     * @param id unique identifier of the tender
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/admin/tender/{id}/update")
    public String showTenderUpdatePage(@PathVariable(value="id") Integer id, ModelMap model) {
        Tender tender = tenderService.findById(id);
        if (tender == null) {
            return "redirect:/admin/tender";
        }

        TenderUpdateDTO tenderUpdateDTO = new TenderUpdateDTO();
        tenderUpdateDTO.setTenderId(tender.getId());
        tenderUpdateDTO.setRefNo(tender.getRefNo());
        tenderUpdateDTO.setTitle(tender.getTitle());
        tenderUpdateDTO.setOpenDate(tender.getOpenDate());
        tenderUpdateDTO.setClosedDate(tender.getClosedDate());
        tenderUpdateDTO.setTenderCategory(tender.getTenderCategory().getId());
        tenderUpdateDTO.setDescription(tender.getDescription());
        tenderUpdateDTO.setTenderType(tender.getTenderType());
        tenderUpdateDTO.setEstimatePurchaseValue(tender.getEstimatePurchaseValue());
        tenderUpdateDTO.setDeliveryDate(tender.getDeliveryDate());
        tenderUpdateDTO.setDeliveryLocation(tender.getDeliveryLocation());
        tenderUpdateDTO.setDeliveryRemarks(tender.getDeliveryRemarks());
        tenderUpdateDTO.setContactPersonName(tender.getContactPersonName());
        tenderUpdateDTO.setContactPersonEmail(tender.getContactPersonEmail());
        tenderUpdateDTO.setContactPersonPhone(tender.getContactPersonPhone());
        tenderUpdateDTO.setDocuments(tender.getDocuments());

        for (TenderItem i : tender.getItems()) {
            TenderItemUpdateDTO item = new TenderItemUpdateDTO();
            item.setId(i.getId());
            item.setUom(i.getUom());
            item.setDescription(i.getDescription());
            item.setQuantity(i.getQuantity());

            tenderUpdateDTO.addTenderItem(item);
        }

        model.addAttribute("tender", tenderUpdateDTO);
        model.addAttribute("tenderType", codeValueService.getByType("tender_type"));
        model.addAttribute("tenderCategories", codeValueService.getAllTenderCategories());
        model.addAttribute("uom", codeValueService.getByType("uom"));
        model.addAttribute("s3Service", s3Service);
        model.addAttribute("maxTenderItemIndex", tender.getItems().size() - 1);
        return "admin/tender/tenderUpdate";
    }

    /**
     * This method is used to update tender information.
     *
     * @param form input data from user
     * @param redirectAttrs RedirectAttributes
     * @param result binding result to check DTO validation
     * @param model ModelMap
     * @return String
     * @see TenderUpdateDTO
     */
    @PostMapping("/admin/tender/update")
    public String updateTender(@Valid @ModelAttribute("tender") TenderUpdateDTO form, RedirectAttributes redirectAttrs,
                               BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            model.addAttribute("tender", form);
            model.addAttribute("tenderType", codeValueService.getByType("tender_type"));
            model.addAttribute("tenderCategories", codeValueService.getAllTenderCategories());
            model.addAttribute("uom", codeValueService.getByType("uom"));
            model.addAttribute("s3Service", s3Service);

            return "redirect:/admin/tender/" + form.getTenderId() + "/update";
        }

        Tender tender = tenderService.findById(form.getTenderId());
        if (tender == null) {
            return "redirect:/admin/tender";
        }

        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        tender.setRefNo(form.getRefNo());
        tender.setTitle(form.getTitle());
        tender.setOpenDate(form.getOpenDate());
        tender.setClosedDate(form.getClosedDate());
        tender.setTenderCategory(codeValueService.getTenderCategoryById(form.getTenderCategory()));
        tender.setDescription(form.getDescription());
        tender.setTenderType(form.getTenderType());
        tender.setEstimatePurchaseValue(form.getEstimatePurchaseValue());
        tender.setDeliveryDate(form.getDeliveryDate());
        tender.setDeliveryLocation(form.getDeliveryLocation());
        tender.setDeliveryRemarks(form.getDeliveryRemarks());
        tender.setContactPersonName(form.getContactPersonName());
        tender.setContactPersonEmail(form.getContactPersonEmail());
        tender.setContactPersonPhone(form.getContactPersonPhone());
        tender.setLastUpdatedBy(usr.getId());
        tender.setLastUpdatedDate(new Date());

        Date current = new Date();
        if(current.after(tender.getClosedDate())){
            tender.setStatus(2);
        }

        try {
            tenderService.updateTender(tender);
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/tender/" + form.getTenderId() + "/update";
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Tender Updated");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/" + form.getTenderId() + "/update";
    }

    /**
     * This method is used to add new tender item.
     *
     * @param form input data from user
     * @param tenderId unique identifier of the tender
     * @param result binding result to check DTO validation
     * @param redirectAttrs RedirectAttributes
     * @return String
     * @see TenderItemUpdateDTO
     */
    @PostMapping("/admin/tender/addTenderItem")
    public String addTenderItem(@Valid TenderItemUpdateDTO form, @RequestParam(name = "tenderId") int tenderId,
                                BindingResult result, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/tender/" + tenderId + "/update";
        }

        Tender tender = tenderService.findById(tenderId);
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TenderItem tenderItem = new TenderItem();
        tenderItem.setUom(form.getUom());
        tenderItem.setDescription(form.getDescription());
        tenderItem.setQuantity(form.getQuantity());
        tenderItem.setCreatedBy(usr.getId());
        tenderItem.setCreatedDate(new Date());
        tenderItem.setLastUpdatedBy(usr.getId());
        tenderItem.setLastUpdatedDate(new Date());
        tenderItem.setTender(tender);

        try {
            tenderItemService.addTenderItem(tenderItem);
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/tender/" + tender.getId() + "/update";
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Tender Item Added");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/" + tenderId + "/update";
    }

    /**
     * This method is used to update tender item.
     *
     * @param tenderId unique identifier of the tender
     * @param tenderItemId unique identifier of the tender item
     * @param form input data from user
     * @param result binding result to check DTO validation
     * @param redirectAttrs RedirectAttributes
     * @return String
     * @see TenderItemUpdateDTO
     */
    @PostMapping("/admin/tender/updateTenderItem")
    public String updateTenderItem(@RequestParam(name = "tenderId") int tenderId, @RequestParam("tenderItemId") int tenderItemId,
                                   @Valid TenderItemUpdateDTO form, BindingResult result, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/tender/" + tenderId + "/update";
        }

        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TenderItem tenderItem = tenderItemService.findTenderItemById(tenderItemId);
        tenderItem.setUom(form.getUom());
        tenderItem.setQuantity(form.getQuantity());
        tenderItem.setDescription(form.getDescription());
        tenderItem.setLastUpdatedBy(usr.getId());
        tenderItem.setLastUpdatedDate(new Date());

        try {
            tenderItemService.updateTenderItem(tenderItem);
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/tender/" + tenderItem.getTender().getId() + "/update";
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Tender Item Updated");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/" + tenderId + "/update";
    }

    /**
     * This method is used to remove tender item.
     *
     * @param tenderItemId unique identifier of the tender item
     * @param tenderId unique identifier of the tender
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/admin/tender/removeTenderItem")
    public String removeTenderItem(@RequestParam(name = "tenderItemId") int tenderItemId, @RequestParam(name = "tenderId") int tenderId,
                                   RedirectAttributes redirectAttrs) {
        tenderItemService.removeTenderItem(tenderItemId);

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Tender Item Removed");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/" + tenderId + "/update";
    }

    /**
     * This method is used to move the tender item sequence up.
     *
     * @param tenderItemId unique identifier of the tender item
     * @param tenderId unique identifier of the tender
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/admin/tender/moveUpTenderItem")
    public String moveUpTenderItem(@RequestParam(name = "tenderItemId") int tenderItemId, @RequestParam(name = "tenderId") int tenderId,
            RedirectAttributes redirectAttrs) {
        tenderItemService.moveUpTenderItem(tenderItemId, tenderId);

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Tender Item Order Updated");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/" + tenderId + "/update";
    }

    /**
     * This method is used to move the tender item sequence down.
     *
     * @param tenderItemId unique identifier of the tender item
     * @param tenderId unique identifier of the tender
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/admin/tender/moveDownTenderItem")
    public String moveDownTenderItem(@RequestParam(name = "tenderItemId") int tenderItemId, @RequestParam(name = "tenderId") int tenderId,
                                   RedirectAttributes redirectAttrs) {
        tenderItemService.moveDownTenderItem(tenderItemId, tenderId);

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Tender Item Order Updated");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/" + tenderId + "/update";
    }

    /**
     * This method is used to add tender document.
     * This method can handle request both from normal HTTP and AJAX.
     * This method will return the name of next screen or null for AJAX response.
     * For AJAX response, this method will return the HTTP status and any error in the HTTP body.
     *
     * @param files new tender document to be added
     * @param tenderId unique identifier of the tender
     * @param resp HttpServletResponse
     * @return String
     * @throws IOException if has exception when putting error message in AJAX response
     */
    @PostMapping("/admin/tender/addTenderDocument")
    public String addTenderDocument(@RequestParam(name = "file") MultipartFile files, @RequestParam(name = "tenderId") int tenderId,
                                    HttpServletResponse resp) throws IOException {
        try {
            Tender tender = tenderService.findById(tenderId);
            if (tender == null) {
                return "redirect:/admin/tender/" + tenderId + "/update";
            }

            CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            tenderService.addTenderDocument(files, tender, usr.getId());

            resp.setStatus(HttpServletResponse.SC_OK);
            return null;
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(ex.getMessage());
            return null;
        }
    }

    /**
     * This method is used to remove tender document from a tender.
     *
     * @param documentId unique identifier of the document
     * @param tenderId unique identifier of the tender
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/admin/tender/removeTenderDocument")
    public String removeTenderDocument(@RequestParam(name = "id") int documentId, @RequestParam(name = "tenderId") int tenderId,
                                       RedirectAttributes redirectAttrs) {
        tenderService.removeTenderDocument(documentId);

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Attachment Removed");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/" + tenderId + "/update";
    }
}
