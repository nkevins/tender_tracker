package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.EvaluateCriteriaDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.EvaluationCriteria;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.EvaluationService;
import com.chlorocode.tendertracker.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by andy on 3/8/2017.
 */
@Controller
public class EvaluationCriteriaController {

    private CodeValueService codeValueService;
    private TenderService tenderService;
    private EvaluationService evaSvc;
    private String className;

    @Autowired
    public EvaluationCriteriaController(CodeValueService codeValueService, EvaluationService evaSvc, TenderService tenderService)
    {
        this.codeValueService = codeValueService;
        this.evaSvc = evaSvc;
        this.tenderService = tenderService;
        this.className = this.getClass().getName();
    }

    @GetMapping("/admin/tender/{tenderid}/setcriteria")
    public String showTenderEvaluationCriteriaPage(@PathVariable(value="tenderid") int tenderId, ModelMap model)
    {
        Tender tender = tenderService.findById(tenderId);
        if (tender == null) {
            TTLogger.error(className, "Unable to find tender, tenderID: " + tenderId);
            return "redirect:/admin/tender";
        }

        model.addAttribute("evaluationType", codeValueService.getByType("Evaluation_Point"));
        List<EvaluationCriteria> lstCriteria = evaSvc.findEvaluationCriteriaByTender(tenderId);

        List<EvaluateCriteriaDTO> dto = new LinkedList<>();
        if (lstCriteria != null) {
            for (EvaluationCriteria c : lstCriteria) {
                EvaluateCriteriaDTO criteria = new EvaluateCriteriaDTO();
                criteria.setId(c.getId());
                criteria.setTenderId(c.getTender().getId());
                criteria.setType(c.getType());
                criteria.setDescription(c.getDescription());
                dto.add(criteria);
            }
        }

        model.addAttribute("tender", tender);
        model.addAttribute("criteriaList", dto);
        model.addAttribute("newCriteria", new EvaluateCriteriaDTO());
        model.addAttribute("tenderId", tenderId);
        return "admin/tender/tenderEvaluationCriteria";
    }

    @PostMapping("/admin/tender/criteria/save")
    public String addTenderCriteria(@Valid EvaluateCriteriaDTO form, RedirectAttributes redirectAttrs) {
        TTLogger.info(className, "Adding new Tender Evaluation Criteria tenderId:" + form.getTenderId() + ", type: " + form.getType() + ", desc: " + form.getDescription());

        Tender tender = tenderService.findById(form.getTenderId());
        if (tender == null) {
            TTLogger.error(className, "Unable to find tender, tenderID: " + form.getTenderId());
            return "redirect:/admin/tender";
        }

        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        EvaluationCriteria eva = new EvaluationCriteria();
        eva.setTender(tender);
        eva.setType(form.getType());
        eva.setDescription(form.getDescription());
        eva.setCreatedBy(usr.getId());
        eva.setCreatedDate(new Date());
        eva.setLastUpdatedBy(usr.getId());
        eva.setLastUpdatedDate(new Date());

        evaSvc.create(eva);

        TTLogger.info(className, "Tender Evaluation Criteria Added");

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Evaluation Criteria Created");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/" + form.getTenderId() + "/setcriteria";
    }

    @PostMapping("/admin/tender/criteria/update")
    public String updateTenderCriteria(@Valid EvaluateCriteriaDTO form, RedirectAttributes redirectAttrs,
                                       @RequestParam("action") String action) {
        if (action.equals("update")) {
            TTLogger.info(className, "Update Tender Evaluation Criteria evalId:" + form.getId() + ", type: " + form.getType() + ", desc: " + form.getDescription());

            EvaluationCriteria eva = evaSvc.findCriteriaById(form.getId());
            if (eva == null) {
                TTLogger.error(className, "Unable to find Evaluation Criteria, evalId: " + form.getId());
                return "redirect:/admin/tender";
            }

            CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            eva.setType(form.getType());
            eva.setDescription(form.getDescription());
            eva.setLastUpdatedBy(usr.getId());
            eva.setLastUpdatedDate(new Date());

            evaSvc.update(eva);

            TTLogger.info(className, "Tender Evaluation Criteria Updated");

            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Evaluation Criteria Updated");
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/tender/" + form.getTenderId() + "/setcriteria";
        } else if (action.equals("delete")) {
            TTLogger.info(className, "Delete Tender Evaluation Criteria evalId:" + form.getId());

            evaSvc.removeEvaluationCriteria(form.getId());

            TTLogger.info(className, "Tender Evaluation Criteria Removed");

            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Evaluation Criteria Removed");
            redirectAttrs.addFlashAttribute("alert", alert);
        }
        return "redirect:/admin/tender/" + form.getTenderId() + "/setcriteria";
    }
}
