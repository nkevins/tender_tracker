package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.EvaluationCriteria;

import java.util.List;

/**
 * Created by andy on 3/8/2017.
 */
public interface EvaluationService {
    List<EvaluationCriteria> findEvaluationCriteriaByIdById(int tenderId);
    EvaluationCriteria create(EvaluationCriteria svc);
}
