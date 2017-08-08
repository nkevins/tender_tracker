package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.EvaluateCriteriaDTO;
import com.chlorocode.tendertracker.dao.dto.TenderClarificationDTO;
import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.service.ClarificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * Created by andy on 8/8/2017.
 */
@Controller
public class TenderClarificationController {
    private ClarificationService clariSvc;

    @Autowired
    public TenderClarificationController(ClarificationService clariSvc){
        this.clariSvc = clariSvc;
    }

    @GetMapping("/admin/tender/clarification")
    public String showTenderClarificationPage() {
        return "admin/clarification/tenderClarificationList";
    }


    @PostMapping("/admin/tender/clarification/update")
    public String updateTenderClarificationResponse(@Valid TenderClarificationDTO form,ModelMap model){

        Clarification clari = clariSvc.updateReponse(form.getId(),form.getResponse());
        if(clari == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Failed to update tender clarification response.Please contact administrator");
            model.addAttribute("alert", alert);
        }else{
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                    "Update tender clarification response successfully");
            model.addAttribute("alert", alert);
        }

        return "admin/clarification/tenderClarificationList";
    }
}
