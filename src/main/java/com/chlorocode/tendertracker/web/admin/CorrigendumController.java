package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.CorrigendumCreateDTO;
import com.chlorocode.tendertracker.dao.dto.CorrigendumUpdateDTO;
import com.chlorocode.tendertracker.dao.entity.Corrigendum;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.CorrigendumService;
import com.chlorocode.tendertracker.service.S3Wrapper;
import com.chlorocode.tendertracker.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;

/**
 * Controller for tender corrigendum page in admin portal.
 */
@Controller
public class CorrigendumController {

    private CorrigendumService corrigendumService;
    private TenderService tenderService;
    private CodeValueService codeValueService;
    private S3Wrapper s3Service;

    /**
     * Constructor.
     *
     * @param corrigendumService CorrigendumService
     * @param tenderService TenderService
     * @param codeValueService CodeValueService
     * @param s3Service S3Wrapper
     */
    @Autowired
    public CorrigendumController(CorrigendumService corrigendumService, TenderService tenderService,
                                 CodeValueService codeValueService, S3Wrapper s3Service) {
        this.corrigendumService = corrigendumService;
        this.tenderService = tenderService;
        this.codeValueService = codeValueService;
        this.s3Service = s3Service;
    }

    /**
     * This method is used to show list of corrigendum for a particular tender.
     *
     * @param id unique identifier of the tender
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/admin/tender/{id}/corrigendum")
    public String showTenderCorrigendum(@PathVariable(value = "id") int id, ModelMap model) {
        Tender tender = tenderService.findById(id);
        if (tender == null) {
            return "redirect:/admin/tender";
        }

        model.addAttribute("tender", tender);
        model.addAttribute("tenderType", codeValueService.getDescription("tender_type", tender.getTenderType()));
        model.addAttribute("corrigendums", corrigendumService.findTenderCorrigendum(tender.getId()));

        return "admin/corrigendum/corrigendumList";
    }

    /**
     * This method is used to display add corrigendum page.
     *
     * @param id unique identifier of the tender
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/admin/tender/{id}/addCorrigendum")
    public String showAddCorrigendumPage(@PathVariable(value = "id") int id, ModelMap model) {
        Tender tender = tenderService.findById(id);
        if (tender == null) {
            return "redirect:/admin/tender";
        }

        CorrigendumCreateDTO dto = new CorrigendumCreateDTO();
        dto.setTenderId(tender.getId());

        model.addAttribute("tender", tender);
        model.addAttribute("corrigendum", dto);

        return "admin/corrigendum/corrigendumAdd";
    }

    /**
     * This method is used to create new corrigendum.
     * This method can handle request both from normal HTTP and AJAX.
     * This method will return the name of next screen or null for AJAX response.
     * For AJAX response, this method will return the HTTP status and any error in the HTTP body.
     *
     * @param form corrigendum data inputted by user
     * @param result this result is used to validate DTO binding validation result
     * @param model ModelMap
     * @param request HttpServletRequest
     * @param resp HttpServletResponse
     * @return String
     * @throws IOException if has exception when putting error message in AJAX response
     * @see CorrigendumCreateDTO
     *
     */
    @PostMapping("/admin/tender/addCorrigendum")
    public String saveCorrigendum(@Valid @ModelAttribute("corrigendum") CorrigendumCreateDTO form,
                                  BindingResult result, ModelMap model, HttpServletRequest request,
                                  HttpServletResponse resp) throws IOException {
        String requestedWith = request.getHeader("X-Requested-With");
        Boolean isAjax = requestedWith != null && "XMLHttpRequest".equals(requestedWith);

        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            model.addAttribute("corrigendum", form);

            if (isAjax) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print(result.getAllErrors().get(0).getDefaultMessage());
                return null;
            } else {
                return "redirect:/admin/tender/" + form.getTenderId() + "/corrigendum";
            }
        }

        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tender tender = tenderService.findById(form.getTenderId());

        Corrigendum corrigendum = new Corrigendum();
        corrigendum.setTender(tender);
        corrigendum.setDescription(form.getDescription());
        corrigendum.setCreatedBy(usr.getId());
        corrigendum.setCreatedDate(new Date());
        corrigendum.setLastUpdatedBy(usr.getId());
        corrigendum.setLastUpdatedDate(new Date());

        corrigendumService.addCorrigendum(corrigendum, form.getAttachments());

        if (isAjax) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print("/admin/tender/" + form.getTenderId() + "/corrigendum");
            return null;
        } else {
            return "redirect:/admin/tender/" + form.getTenderId() + "/corrigendum";
        }
    }

    /**
     * This method is used to remove corrigendum.
     *
     * @param corrigendumId unique identifier of the corrigendum
     * @param tenderId unique identifier of the tender
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/admin/tender/removeCorrigendum")
    public String removeCorrigendum(@RequestParam("corrigendumId") int corrigendumId, @RequestParam("tenderId") int tenderId,
                                    RedirectAttributes redirectAttrs) {
        corrigendumService.removeCorrigendum(corrigendumId);

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Corrigendum Deleted");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/" + tenderId + "/corrigendum";
    }

    /**
     * This method is used to display edit corrigendum page.
     *
     * @param id unique identififer of the corrigendum
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/admin/tender/corrigendum/{id}/edit")
    public String showEditCorrigendumPage(@PathVariable(value = "id") int id, ModelMap model) {
        Corrigendum corrigendum = corrigendumService.findCorrigendumById(id);
        if (corrigendum == null) {
            return "redirct:/admin/tender";
        }

        CorrigendumUpdateDTO dto = new CorrigendumUpdateDTO();
        dto.setId(corrigendum.getId());
        dto.setDescription(corrigendum.getDescription());
        dto.setTender(corrigendum.getTender());
        dto.setDocuments(corrigendum.getDocuments());

        model.addAttribute("corrigendum", dto);
        model.addAttribute("s3Service", s3Service);

        return "admin/corrigendum/corrigendumEdit";
    }

    /**
     * This method is used to edit corrigendum.
     *
     * @param form corrigendum data inputted by user
     * @param result this result is used to validate DTO binding validation result
     * @param model ModelMap
     * @param redirectAttrs RedirectAttributes
     * @return String
     * @see CorrigendumUpdateDTO
     */
    @PostMapping("/admin/tender/editCorrigendum")
    public String editCorrigendum(@Valid @ModelAttribute("corrigendum") CorrigendumUpdateDTO form, BindingResult result,
                                  ModelMap model, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            model.addAttribute("corrigendum", form);

            return "redirect:/admin/tender/corrigendum/" + form.getId() + "/edit";
        }

        Corrigendum corrigendum = corrigendumService.findCorrigendumById(form.getId());
        if (corrigendum == null) {
            return "redirect:/admin/tender";
        }

        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        corrigendum.setDescription(form.getDescription());
        corrigendum.setLastUpdatedBy(usr.getId());
        corrigendum.setLastUpdatedDate(new Date());

        corrigendumService.updateCorrigendum(corrigendum);

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Corrigendum Updated");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/" + corrigendum.getTender().getId() + "/corrigendum";
    }

    /**
     * This method is used to add new document to a corrigendum via AJAX.
     *
     * @param files document to be added
     * @param corrigendumId unique identifier of the corrigendum
     * @param resp HttpServletResponse
     * @return String
     * @throws IOException if unable to write error message into response body
     */
    @PostMapping("/admin/tender/addCorrigendumDocument")
    public String addCorrigendumDocument(@RequestParam(name = "file") MultipartFile files,
                                         @RequestParam(name = "id") int corrigendumId,
                                         HttpServletResponse resp) throws IOException {
        try {
            Corrigendum corrigendum = corrigendumService.findCorrigendumById(corrigendumId);
            if (corrigendum == null) {
                return "redirect:/admin/tender";
            }

            CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            corrigendumService.addCorrigendumDocument(files, corrigendum, usr.getId());

            resp.setStatus(HttpServletResponse.SC_OK);
            return null;
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(ex.getMessage());
            return null;
        }
    }

    /**
     * This method is used to remove a document from a corrigendum.
     *
     * @param documentId unique identifier of the document
     * @param corrigendumId unique identifier of the corrigendum
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/tender/removeCorrigendumDocument")
    public String removeCorrigendumDocument(@RequestParam(name = "corrigendumDocumentId") int documentId,
                                            @RequestParam(name = "corrigendumId") int corrigendumId,
                                            RedirectAttributes redirectAttrs) {
        corrigendumService.removeCorrigendumDocument(documentId);

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Attachment Removed");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/corrigendum/" + corrigendumId + "/edit";
    }
}
