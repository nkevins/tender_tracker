package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.EvaluationCriteriaDAO;
import com.chlorocode.tendertracker.dao.EvaluationResultDAO;
import com.chlorocode.tendertracker.dao.entity.EvaluationCriteria;
import com.chlorocode.tendertracker.dao.entity.EvaluationResult;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by andy on 3/8/2017.
 */
@Service
public class EvaluationServiceImpl implements EvaluationService {
    private EvaluationCriteriaDAO evaDao;
    private EvaluationResultDAO evaResDao;

    private EvaluationServiceImpl(EvaluationCriteriaDAO evaDao, EvaluationResultDAO evaResDao)
    {
        this.evaDao = evaDao;
        this.evaResDao = evaResDao;
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

    @Override
    public void removeEvaluationCriteria(int id) {
        EvaluationCriteria evaluationCriteria = evaDao.findOne(id);
        evaDao.delete(evaluationCriteria);
    }

    @Override
    public boolean isDuplicateEvaluation(int bidId, int userId) {
        List<EvaluationResult> results = evaResDao.findEvaluationResultByBidAndEvaluator(bidId, userId);
        if (results.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void saveEvaluationResult(List<EvaluationResult> results) {
        if (isDuplicateEvaluation(results.get(0).getBid().getId(), results.get(0).getEvaluator().getId())) {
            throw new ApplicationException("Evaluator has submitted evaluation result before.");
        }

        evaResDao.save(results);
    }
}
