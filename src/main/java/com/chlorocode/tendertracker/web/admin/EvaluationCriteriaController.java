package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.EvaluateCriteriaDTO;
import com.chlorocode.tendertracker.dao.entity.EvaluationCriteria;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.EvaluationService;
import org.hibernate.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Created by andy on 3/8/2017.
 */
@Controller
public class EvaluationCriteriaController {

    private CodeValueService codeValueService;
    private EvaluationService evaSvc;

    @Autowired
    public EvaluationCriteriaController(CodeValueService codeValueService,EvaluationService evaSvc)
    {
        this.codeValueService = codeValueService;
        this.evaSvc = evaSvc;
    }

    @GetMapping("/admin/tender/setcriteria/{tenderid}")
    public String showTenderEvaluationCriteriaPage(@PathVariable(value="tenderid") int tenderId, ModelMap model)
    {
        List<EvaluationCriteria>  lstCriteria = evaSvc.findEvaluationCriteriaByIdById(tenderId);
        EvaluateCriteriaDTO criteriaDto = new EvaluateCriteriaDTO();
        model.addAttribute("criteria",criteriaDto);
        model.addAttribute("evaluationType", codeValueService.getByType("Evaluation_Point"));
        return "admin/tender/tenderEvaluationCriteria";
    }
}
