package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDTO;
import com.chlorocode.tendertracker.dao.dto.EvaluateCriteriaDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.EvaluationCriteria;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.EvaluationService;
import org.hibernate.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;

/**
 * Created by andy on 3/8/2017.
 */
@Controller
public class EvaluationCriteriaController {

    private CodeValueService codeValueService;
    private EvaluationService evaSvc;
    private String className;

    @Autowired
    public EvaluationCriteriaController(CodeValueService codeValueService,EvaluationService evaSvc)
    {
        this.codeValueService = codeValueService;
        this.evaSvc = evaSvc;
        this.className = this.getClass().getName();
    }

    @PostMapping("/admin/tender/criteria4/save")
    public String saveTenderCriteria4( ModelMap model,@Valid @ModelAttribute("criteria") EvaluateCriteriaDTO form,HttpServletRequest request)
    {
        HttpSession sess = request.getSession();
        if(sess.getAttribute("tenderId") == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"tender id is null");
            return "admin/tender/tenderEvaluationCriteria";
        }

        if(sess.getAttribute("evaluationCriteriaDto") == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"evaluationCriteria object is null");
            return "admin/tender/tenderEvaluationCriteria";
        }
        EvaluateCriteriaDTO sessionDto = (EvaluateCriteriaDTO) sess.getAttribute("evaluationCriteriaDto");
        int tenderId = (int) sess.getAttribute("tenderId");
        model.addAttribute("evaluationType", codeValueService.getByType("Evaluation_Point"));

        try{
            CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            EvaluationCriteria eva = new EvaluationCriteria();
            Tender tender = new Tender();

            if(sessionDto.getId4() > 0){
                EvaluationCriteria dbEva = evaSvc.findCriteriaById(sessionDto.getId4());
                dbEva.setType(form.getType4());
                dbEva.setDescription(form.getCriteria4());
                dbEva.setLastUpdatedBy(usr.getId());
                dbEva.setLastUpdatedDate(new Date());
                evaSvc.update(dbEva);
            }else{
                //save 4th criteria
                eva = new EvaluationCriteria();
                eva.setCreatedBy(usr.getId());
                eva.setLastUpdatedBy(usr.getId());
                eva.setCreatedDate(new Date());
                tender = new Tender();
                tender.setId(tenderId);
                eva.setTender(tender);
                eva.setLastUpdatedDate(new Date());
                eva.setDescription(form.getCriteria4());
                eva.setType(form.getType4());
                evaSvc.create(eva);
            }
            List<EvaluationCriteria>  lstCriteria = evaSvc.findEvaluationCriteriaByIdById(tenderId);
            EvaluateCriteriaDTO evaDto = setEvaluationCriteria(tenderId,lstCriteria);
            model.addAttribute("criteria", evaDto);
            sess.setAttribute("evaluationCriteriaDto",evaDto);
        }catch(Exception ex){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"error occur when save tender criteria.Please contact administrator",ex);
            return "admin/tender/tenderEvaluationCriteria";
        }
        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "Tender criteria save successfully");
        model.addAttribute("alert", alert);

        return "admin/tender/tenderEvaluationCriteria";
    }

    @PostMapping("/admin/tender/criteria3/save")
    public String saveTenderCriteria3( ModelMap model,@Valid @ModelAttribute("criteria") EvaluateCriteriaDTO form,HttpServletRequest request)
    {
        HttpSession sess = request.getSession();
        if(sess.getAttribute("tenderId") == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"tender id is null");
            return "admin/tender/tenderEvaluationCriteria";
        }

        if(sess.getAttribute("evaluationCriteriaDto") == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"evaluationCriteria object is null");
            return "admin/tender/tenderEvaluationCriteria";
        }
        EvaluateCriteriaDTO sessionDto = (EvaluateCriteriaDTO) sess.getAttribute("evaluationCriteriaDto");
        int tenderId = (int) sess.getAttribute("tenderId");
        model.addAttribute("evaluationType", codeValueService.getByType("Evaluation_Point"));

        try{
            CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            EvaluationCriteria eva = new EvaluationCriteria();
            Tender tender = new Tender();

            if(sessionDto.getId3() > 0){
                EvaluationCriteria dbEva = evaSvc.findCriteriaById(sessionDto.getId3());
                dbEva.setType(form.getType3());
                dbEva.setDescription(form.getCriteria3());
                dbEva.setLastUpdatedBy(usr.getId());
                dbEva.setLastUpdatedDate(new Date());
                evaSvc.update(dbEva);
            }else{
                //save 3rd criteria
                eva = new EvaluationCriteria();
                eva.setCreatedBy(usr.getId());
                eva.setLastUpdatedBy(usr.getId());
                eva.setCreatedDate(new Date());
                tender = new Tender();
                tender.setId(tenderId);
                eva.setTender(tender);
                eva.setLastUpdatedDate(new Date());
                eva.setDescription(form.getCriteria3());
                eva.setType(form.getType3());
                evaSvc.create(eva);
            }
            List<EvaluationCriteria>  lstCriteria = evaSvc.findEvaluationCriteriaByIdById(tenderId);
            EvaluateCriteriaDTO evaDto = setEvaluationCriteria(tenderId,lstCriteria);
            model.addAttribute("criteria", evaDto);
            sess.setAttribute("evaluationCriteriaDto",evaDto);
        }catch(Exception ex){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"error occur when save tender criteria.Please contact administrator",ex);
        }
        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "Tender criteria save successfully");
        model.addAttribute("alert", alert);

        return "admin/tender/tenderEvaluationCriteria";
    }

    @PostMapping("/admin/tender/criteria2/save")
    public String saveTenderCriteria2( ModelMap model,@Valid @ModelAttribute("criteria") EvaluateCriteriaDTO form,HttpServletRequest request)
    {
        HttpSession sess = request.getSession();
        if(sess.getAttribute("tenderId") == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"tender id is null");
            return "admin/tender/tenderEvaluationCriteria";
        }

        if(sess.getAttribute("evaluationCriteriaDto") == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"evaluationCriteria object is null");
            return "admin/tender/tenderEvaluationCriteria";
        }

        EvaluateCriteriaDTO sessionDto = (EvaluateCriteriaDTO) sess.getAttribute("evaluationCriteriaDto");
        int tenderId = (int) sess.getAttribute("tenderId");
        model.addAttribute("evaluationType", codeValueService.getByType("Evaluation_Point"));

        try{
            CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            EvaluationCriteria eva = new EvaluationCriteria();
            Tender tender = new Tender();

            if(sessionDto.getId2() > 0){
                EvaluationCriteria dbEva = evaSvc.findCriteriaById(sessionDto.getId2());
                dbEva.setType(form.getType2());
                dbEva.setDescription(form.getCriteria2());
                dbEva.setLastUpdatedBy(usr.getId());
                dbEva.setLastUpdatedDate(new Date());
                evaSvc.update(dbEva);
            }else {
                //save 2nd criteria
                eva = new EvaluationCriteria();
                eva.setCreatedBy(usr.getId());
                eva.setLastUpdatedBy(usr.getId());
                eva.setCreatedDate(new Date());
                tender = new Tender();
                tender.setId(tenderId);
                eva.setTender(tender);
                eva.setLastUpdatedDate(new Date());
                eva.setDescription(form.getCriteria2());
                eva.setType(form.getType2());
                evaSvc.create(eva);
            }
            List<EvaluationCriteria>  lstCriteria = evaSvc.findEvaluationCriteriaByIdById(tenderId);
            EvaluateCriteriaDTO evaDto = setEvaluationCriteria(tenderId,lstCriteria);
            model.addAttribute("criteria", evaDto);
            sess.setAttribute("evaluationCriteriaDto",evaDto);
        }catch(Exception ex){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"error occur when save tender criteria.Please contact administrator",ex);
        }
        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "Tender criteria save successfully");
        model.addAttribute("alert", alert);

        return "admin/tender/tenderEvaluationCriteria";
    }
    @PostMapping("/admin/tender/criteria1/save")
    public String saveTenderCriteria1( ModelMap model,@Valid @ModelAttribute("criteria") EvaluateCriteriaDTO form,HttpServletRequest request)
    {
        HttpSession sess = request.getSession();
        if(sess.getAttribute("tenderId") == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"tender id is null");
            return "admin/tender/tenderEvaluationCriteria";
        }

        if(sess.getAttribute("evaluationCriteriaDto") == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"evaluationCriteria object is null");
            return "admin/tender/tenderEvaluationCriteria";
        }

        EvaluateCriteriaDTO sessionDto = (EvaluateCriteriaDTO) sess.getAttribute("evaluationCriteriaDto");
        int tenderId = (int) sess.getAttribute("tenderId");
        model.addAttribute("evaluationType", codeValueService.getByType("Evaluation_Point"));

        try{
            CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            EvaluationCriteria eva = new EvaluationCriteria();
            Tender tender = new Tender();
            //save 1st criteria
            if(sessionDto.getId1() > 0 ){
                EvaluationCriteria dbEva = evaSvc.findCriteriaById(sessionDto.getId1());
                dbEva.setType(form.getType1());
                dbEva.setDescription(form.getCriteria1());
                dbEva.setLastUpdatedBy(usr.getId());
                dbEva.setLastUpdatedDate(new Date());
                evaSvc.update(dbEva);
            }else if(form.getCriteria1().length() > 0){
                eva.setCreatedBy(usr.getId());
                eva.setLastUpdatedBy(usr.getId());
                eva.setCreatedDate(new Date());
                tender.setId(tenderId);
                eva.setTender(tender);
                eva.setLastUpdatedDate(new Date());
                eva.setDescription(form.getCriteria1());
                eva.setType(form.getType1());
                evaSvc.create(eva);
            }
            List<EvaluationCriteria>  lstCriteria = evaSvc.findEvaluationCriteriaByIdById(tenderId);
            EvaluateCriteriaDTO evaDto = setEvaluationCriteria(tenderId,lstCriteria);
            model.addAttribute("criteria", evaDto);
            sess.setAttribute("evaluationCriteriaDto",evaDto);

        }catch(Exception ex){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"error occur when save tender criteria.Please contact administrator",ex);
            return "admin/tender/tenderEvaluationCriteria";
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "Tender criteria save successfully");
        model.addAttribute("alert", alert);
        return "admin/tender/tenderEvaluationCriteria";
    }

    private EvaluateCriteriaDTO setEvaluationCriteria(int tenderId,List<EvaluationCriteria>  lstCriteria){
        EvaluateCriteriaDTO  criteriaDto = new EvaluateCriteriaDTO();
        try{
            //List<EvaluationCriteria>  lstCriteria = evaSvc.findEvaluationCriteriaByIdById(tenderId);

            for(int i = 0; i < lstCriteria.size(); i++ ){
                EvaluationCriteria e1 = new EvaluationCriteria();
                e1 =  lstCriteria.get(i);

                if(i==0){
                    criteriaDto.setType1(e1.getType());
                    criteriaDto.setCriteria1(e1.getDescription());
                    criteriaDto.setId1(e1.getId());
                }else if(i == 1){
                    criteriaDto.setType2(e1.getType());
                    criteriaDto.setCriteria2(e1.getDescription());
                    criteriaDto.setId2(e1.getId());
                }else if(i==2){
                    criteriaDto.setType3(e1.getType());
                    criteriaDto.setCriteria3(e1.getDescription());
                    criteriaDto.setId3(e1.getId());
                }else if(i==3){
                    criteriaDto.setType4(e1.getType());
                    criteriaDto.setCriteria4(e1.getDescription());
                    criteriaDto.setId4(e1.getId());
                }
            }
        }catch(Exception ex){
            TTLogger.error(className,"error in set tender criteria", ex);
            return null;
        }
        return criteriaDto;
    }
    @GetMapping("/admin/tender/setcriteria/{tenderid}")
    public String showTenderEvaluationCriteriaPage(@PathVariable(value="tenderid") int tenderId, ModelMap model,HttpServletRequest request)
    {
        HttpSession sess = request.getSession();
        sess.setAttribute("tenderId", tenderId);
        model.addAttribute("evaluationType", codeValueService.getByType("Evaluation_Point"));
        EvaluateCriteriaDTO criteriaDto = null;

        try{
            List<EvaluationCriteria>  lstCriteria = evaSvc.findEvaluationCriteriaByIdById(tenderId);
            criteriaDto = new EvaluateCriteriaDTO();
            if(lstCriteria == null || lstCriteria.size() == 0)
            {
                model.addAttribute("criteria",criteriaDto);
                model.addAttribute("evaluationType", codeValueService.getByType("Evaluation_Point"));
            }else{
                criteriaDto = setEvaluationCriteria(tenderId,lstCriteria);
                if(criteriaDto == null){
                    AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                            "Error occur, please contact administrator");
                    model.addAttribute("alert", alert);
                    TTLogger.error(className,"Failed to get evaluation from database");
                    return "admin/tender/tenderEvaluationCriteria";
                }
                model.addAttribute("criteria",criteriaDto);
                model.addAttribute("evaluationType", codeValueService.getByType("Evaluation_Point"));
            }
        }catch(Exception ex){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Error occur, please contact administrator");
            model.addAttribute("alert", alert);
            TTLogger.error(className,"error in set tender criteria", ex);
        }

        sess.setAttribute("evaluationCriteriaDto",criteriaDto);
        return "admin/tender/tenderEvaluationCriteria";
    }
}
