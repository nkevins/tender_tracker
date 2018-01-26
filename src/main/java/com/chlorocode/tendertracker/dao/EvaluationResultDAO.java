package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.EvaluationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This DAO is used for EvaluationResult.
 */
@Repository
public interface EvaluationResultDAO extends JpaRepository<EvaluationResult, Integer> {
    /**
     * This method is used to find the EvaluationResult by bid and evaluator.
     *
     * @param bidId unique identifier of the bid
     * @param evaluatorId unique identifier of the evaluator
     * @return List
     * @see EvaluationResult
     */
    @Query("select r from EvaluationResult r where r.bid.id = ?1 and r.evaluator.id = ?2")
    List<EvaluationResult> findEvaluationResultByBidAndEvaluator(int bidId, int evaluatorId);

    /**
     * This method is used to get the average evaluation score of the bid.
     *
     * @param bidId unique identifier of the bid
     * @return Double
     */
    @Query("select avg(r.result) from EvaluationResult r where r.bid.id = ?1 and r.criteria.type = 1")
    Double getBidAverageEvaluationScore(int bidId);

    /**
     * This method is used to find the average evaluation score of bid criteria.
     *
     * @param bidId unique identifier of the bid
     * @param criteriaId unique identifier of criteria
     * @return Double
     */
    @Query("select avg(r.result) from EvaluationResult r where r.bid.id = ?1 and r.criteria.id = ?2 and r.criteria.type = 1")
    Double getBidCriteriaAverageEvaluationScore(int bidId, int criteriaId);

    /**
     * This method is used to get the dual criteria evaluation count.
     *
     * @param bidId unique identifier of the bid
     * @param criteriaId unique identifier of criteria
     * @param result result of evaluation
     * @return Integer
     */
    @Query("select count(r) from EvaluationResult r where r.bid.id = ?1 and r.criteria.id = ?2 and r.result = ?3 and r.criteria.type = 2")
    Integer getDualCriteriaEvaluationCount(int bidId, int criteriaId, int result);
}
