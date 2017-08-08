package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.EvaluateCriteriaDTO;
import com.chlorocode.tendertracker.dao.dto.TenderClarificationDTO;
import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.CodeValue;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.ClarificationService;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by andy on 8/8/2017.
 */
@Controller
public class TenderClarificationController {
    private ClarificationService clariSvc;
    private UserService userSvc;
    private String className;
    private CodeValueService codeValueService;

    @Autowired
    public TenderClarificationController(ClarificationService clariSvc,UserService userSvc,CodeValueService codeValueService){
        this.clariSvc = clariSvc;
        this.userSvc = userSvc;
        className = this.getClass().getName();
        this.codeValueService = codeValueService;
    }

    @GetMapping("/admin/tender/clarification")
    public String showTenderClarificationPage() {
        return "admin/clarification/tenderClarificationList";
    }

    @GetMapping("/admin/tender/clarification/view/{id}")
    public String showTenderClarificationUpdatePage(@PathVariable(value = "id") Integer id,ModelMap model) {
        Clarification clarfi = clariSvc.findById(id);
        if(clarfi == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "unable to find the clarification based on this id " + id);
            model.addAttribute("alert", alert);
            return "admin/clarification/tenderClarificationList";
        }
        try{
            User usr = userSvc.findById(clarfi.getCreatedBy());
            TenderClarificationDTO dto = new TenderClarificationDTO();
            dto.setId(clarfi.getId());
            dto.setCategory(clarfi.getTender().getTenderCategory().getName());
            dto.setClarification(clarfi.getDescription());
            dto.setClosedDate(clarfi.getTender().getOpenDate());
            dto.setRefNo(clarfi.getTender().getRefNo());
            dto.setOpenDate(clarfi.getTender().getOpenDate());
            dto.setTitle(clarfi.getTender().getTitle());
            dto.setSubmittedByCompany(clarfi.getCompany().getName());
            dto.setSubmittedByName(usr.getName());
            dto.setSubmittedByContactNo(usr.getContactNo());
            dto.setSubmittedByEmail(usr.getEmail());
            dto.setSubmittedDate(clarfi.getCreatedDate());
            List<CodeValue> lstCode = codeValueService.getByType("tender_type");
            for(int i = 0; i < lstCode.size(); i++){
                CodeValue code = lstCode.get(i);
                if(code.getCode() == clarfi.getTender().getTenderType()){
                    dto.setTenderType(code.getDescription());
                    break;
                }
            }

            //dto.setTenderType(clarfi.getTender().getTenderType());
            model.addAttribute("clarificationDto",dto);
        }catch(Exception ex){
            TTLogger.error(className,"error: ", ex );
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Failed to retrieve tender clarification.Please contact administrator");
            model.addAttribute("alert", alert);
            return "admin/clarification/tenderClarificationList";
        }


        return "admin/clarification/tenderClarificationView";
    }

    @PostMapping("/tender/clarification/save")
    public String saveTenderClarification(ModelMap model,@Valid TenderClarificationDTO form,
                                          @RequestParam("tenderId") int tenderId,@RequestParam("companyId") int companyId){
        Tender td = new Tender();
        Clarification clar = clariSvc.create(form.getResponse(),tenderId,companyId);

        if(clar == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Failed to save tender clarification response.Please contact administrator");
            model.addAttribute("alert", alert);
        }else{
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                    "Save tender clarification response successfully");
            model.addAttribute("alert", alert);
        }
        return "redirect:/tender/" + tenderId;
       // return "tenderDetails";
    }

    @PostMapping("/admin/tender/clarification/update")
    public String updateTenderClarificationResponse(@Valid TenderClarificationDTO form, ModelMap model){

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
