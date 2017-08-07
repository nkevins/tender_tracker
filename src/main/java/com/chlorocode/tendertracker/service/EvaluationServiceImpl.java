package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.EvaluationCriteriaDAO;
import com.chlorocode.tendertracker.dao.entity.EvaluationCriteria;
import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by andy on 3/8/2017.
 */
@Service
public class EvaluationServiceImpl implements EvaluationService {
    private EvaluationCriteriaDAO evaDao;

    private EvaluationServiceImpl(EvaluationCriteriaDAO evaDao)
    {
        this.evaDao = evaDao;
    }

    @Override
    public List<EvaluationCriteria> findEvaluationCriteriaByTender(int tenderId) {
        return evaDao.findEvaluationCriteriaByTender(tenderId);
    }

    public EvaluationCriteria create(EvaluationCriteria svc){
       return evaDao.saveAndFlush(svc);
    }

    public EvaluationCriteria update(EvaluationCriteria svc){
        return evaDao.save(svc);
    }

    public EvaluationCriteria findCriteriaById(int id){
        return evaDao.findOne(id);
    }


}
