package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.MilestoneDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Milestone;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.MilestoneService;
import com.chlorocode.tendertracker.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Controller for tender milestone page.
 */
@Controller
public class MilestoneController {

    private CodeValueService codeValueService;
    private TenderService tenderService;
    private MilestoneService milestoneService;
    private String className;

    /**
     * Constructor.
     *
     * @param codeValueService CodeValueService
     * @param milestoneService MilestoneService
     * @param tenderService TenderService
     */
    @Autowired
    public MilestoneController(CodeValueService codeValueService, MilestoneService milestoneService, TenderService tenderService)
    {
        this.codeValueService = codeValueService;
        this.milestoneService = milestoneService;
        this.tenderService = tenderService;
        this.className = this.getClass().getName();
    }

    /**
     * This method is used to display tender milestone list page.
     *
     * @param tenderId unique identifier of the tender
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/admin/tender/{tenderid}/setmilestone")
    public String showTenderMilestonePage(@PathVariable(value="tenderid") int tenderId, ModelMap model)
    {
        Tender tender = tenderService.findById(tenderId);
        if (tender == null) {
            TTLogger.error(className, "Unable to find tender, tenderID: " + tenderId);
            return "redirect:/admin/tender";
        }

        model.addAttribute("statusList", codeValueService.getByType("milestone_status"));
        List<Milestone> milestones = milestoneService.findMilestoneByTender(tenderId);

        List<MilestoneDTO> dto = new LinkedList<>();
        if (milestones != null) {
            for (Milestone c : milestones) {
                MilestoneDTO milestoneDTO = new MilestoneDTO();
                milestoneDTO.setId(c.getId());
                milestoneDTO.setTenderId(c.getTender().getId());
                milestoneDTO.setStatus(c.getStatus());
                milestoneDTO.setDueDate(c.getDueDate());
                milestoneDTO.setDescription(c.getDescription());
                dto.add(milestoneDTO);
            }
        }
        model.addAttribute("tender", tender);
        model.addAttribute("milestones", dto);
        model.addAttribute("newMilestone", new MilestoneDTO());
        model.addAttribute("tenderId", tenderId);
        model.addAttribute("dateFormat", dateFormat());
        return "admin/tender/tenderMilestone";
    }

    /**
     * This method is used to add new tender milestone.
     *
     * @param form milestone data inputted by user
     * @param redirectAttrs RedirectAttributes
     * @return String
     * @see MilestoneDTO
     */
    @PostMapping("/admin/tender/milestone/save")
    public String addMilestone(@Valid @ModelAttribute("newMilestone") MilestoneDTO form, RedirectAttributes redirectAttrs) {
        TTLogger.info(className, "Adding new Tender Milestone tenderId:" + form.getTenderId() + ", status: " + form.getStatus() + ", desc: " + form.getDescription());

        Tender tender = tenderService.findById(form.getTenderId());
        if (tender == null) {
            TTLogger.error(className, "Unable to find tender, tenderID: " + form.getTenderId());
            return "redirect:/admin/tender";
        }

        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Milestone milestone = new Milestone();
        milestone.setTender(tender);
        milestone.setNotifyStatus(0);
        milestone.setStatus(form.getStatus());
        milestone.setDueDate(form.getDueDate());
        milestone.setDescription(form.getDescription());
        milestone.setCreatedBy(usr.getId());
        milestone.setCreatedDate(new Date());
        milestone.setLastUpdatedBy(usr.getId());
        milestone.setLastUpdatedDate(new Date());

        milestoneService.create(milestone);

        TTLogger.info(className, "Tender Milestone Added="+milestone);

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Milestone Created");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/tender/" + form.getTenderId() + "/setmilestone";
    }

    /**
     * This method is used to update / delete tender milestone.
     *
     * @param form milestone data inputted by user
     * @param redirectAttrs RedirectAttributes
     * @param action update / delete indicator for the action to be performed
     * @return String
     * @see MilestoneDTO
     */
    @PostMapping("/admin/tender/milestone/update")
    public String updateMilestone(@Valid MilestoneDTO form, RedirectAttributes redirectAttrs,
                                       @RequestParam("action") String action) {
        if (action.equals("update")) {
            TTLogger.info(className, "Update Tender Milestone evalId:" + form.getId() + ", status: " + form.getStatus() + ", desc: " + form.getDescription());

            Milestone milestone = milestoneService.findMilestoneById(form.getId());
            if (milestone == null) {
                TTLogger.error(className, "Unable to find Milestone, milestoneId: " + form.getId());
                return "redirect:/admin/tender";
            }

            CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            milestone.setStatus(form.getStatus());
            if (!milestone.getDueDate().equals(form.getDueDate())) {
                // Reset notify status to resend notification for milestone.
                milestone.setNotifyStatus(0);
            }
            milestone.setDueDate(form.getDueDate());
            milestone.setDescription(form.getDescription());
            milestone.setLastUpdatedBy(usr.getId());
            milestone.setLastUpdatedDate(new Date());

            milestoneService.update(milestone);

            TTLogger.info(className, "Tender Milestone Updated");

            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Milestone Updated");
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/tender/" + form.getTenderId() + "/setmilestone";
        } else if (action.equals("delete")) {
            TTLogger.info(className, "Delete Tender Milestone milestoneId:" + form.getId());

            milestoneService.removeMilestone(form.getId());

            TTLogger.info(className, "Tender Milestone Removed");

            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Milestone Removed");
            redirectAttrs.addFlashAttribute("alert", alert);
        }
        return "redirect:/admin/tender/" + form.getTenderId() + "/setmilestone";
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

    @ModelAttribute("dateFormat")
    public String dateFormat() {
        return "dd/MM/yyyy HH:mm";
    }
}
