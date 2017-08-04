package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDTO;
import com.chlorocode.tendertracker.dao.dto.EvaluateCriteriaDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.EvaluationCriteria;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.EvaluationService;
import org.hibernate.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Date;
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

    @PostMapping("/admin/tender/criteria/save")
    public String saveTenderCriteria( ModelMap model,@Valid @ModelAttribute("criteria") EvaluateCriteriaDTO form)
    {
        EvaluationCriteria eva = new EvaluationCriteria();
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        eva.setCreatedBy(usr.getId());
        eva.setLastUpdatedBy(usr.getId());
        eva.setCreatedDate(new Date());
        Tender tender = new Tender();
        tender.setId(1);
        eva.setTender(tender);
        eva.setLastUpdatedDate(new Date());
        eva.setDescription(form.getCriteria1());
        eva.setType(form.getType1());
        evaSvc.create(eva);
        return "";
    }

    @GetMapping("/admin/tender/setcriteria/{tenderid}")
    public String showTenderEvaluationCriteriaPage(@PathVariable(value="tenderid") int tenderId, ModelMap model)
    {
        List<EvaluationCriteria>  lstCriteria = evaSvc.findEvaluationCriteriaByIdById(tenderId);
        EvaluateCriteriaDTO criteriaDto = null;
        if(lstCriteria == null || lstCriteria.size() == 0)
        {
            criteriaDto = new EvaluateCriteriaDTO();
            model.addAttribute("criteria",criteriaDto);
            model.addAttribute("evaluationType", codeValueService.getByType("Evaluation_Point"));
        }else{
            for(int i = 0; i < lstCriteria.size(); i++ ){
                criteriaDto = new EvaluateCriteriaDTO();
                EvaluationCriteria e1 = lstCriteria.get(i);

                if(i==0){
                    criteriaDto.setType1(e1.getType());
                    criteriaDto.setCriteria1(e1.getDescription());
                }else if(i == 1){
                    criteriaDto.setType2(e1.getType());
                    criteriaDto.setCriteria2(e1.getDescription());
                }else if(i==2){
                    criteriaDto.setType3(e1.getType());
                    criteriaDto.setCriteria3(e1.getDescription());
                }else if(i==3){
                    criteriaDto.setType4(e1.getType());
                    criteriaDto.setCriteria4(e1.getDescription());
                }
            }
        }

        return "admin/tender/tenderEvaluationCriteria";
    }
}
