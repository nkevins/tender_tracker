package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.EvaluationCriteria;
import com.chlorocode.tendertracker.dao.entity.EvaluationResult;

import java.util.List;

/**
 * Service interface for tender evaluation.
 */
public interface EvaluationService {
    /**
     * This method is used to find all evaluation criteria for a particular tender.
     *
     * @param tenderId unique identifier of the tender
     * @return list of EvaluationCriteria
     */
    List<EvaluationCriteria> findEvaluationCriteriaByTender(int tenderId);

    /**
     * This method is used to create new evaluation criteria.
     *
     * @param evaluationCriteria evaluation criteria to be created
     * @return Evaluation Criteria
     */
    EvaluationCriteria create(EvaluationCriteria evaluationCriteria);

    /**
     * This method is used to find evaluation criteria by id.
     *
     * @param id unique identifier of the evaluation criteria
     * @return EvaluationCriteria
     */
    EvaluationCriteria findCriteriaById(int id);

    /**
     * This method is used to update evaluation criteria.
     *
     * @param evaluationCriteria evaluation criteria to be updated
     * @return EvaluationCriteria
     */
    EvaluationCriteria update(EvaluationCriteria evaluationCriteria);

    /**
     * This method is used to remove evaluation criteria.
     *
     * @param id unique identifier of the evaluation criteria
     */
    void removeEvaluationCriteria(int id);

    /**
     * This method is used to check if the same user has submitted an tender evaluation for a particular bid before.
     *
     * @param bidId unique identifier of the bid
     * @param userId unique identifier of the user
     * @return boolean
     */
    boolean isDuplicateEvaluation(int bidId, int userId);

    /**
     * This method is used to save tender evaluation result.
     *
     * @param results list of tender evaluation to be saved
     */
    void saveEvaluationResult(List<EvaluationResult> results);
}
