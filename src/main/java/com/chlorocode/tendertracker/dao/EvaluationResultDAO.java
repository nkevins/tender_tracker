package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.EvaluationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationResultDAO extends JpaRepository<EvaluationResult, Integer> {

    @Query("select r from EvaluationResult r where r.bid.id = ?1 and r.evaluator.id = ?2")
    List<EvaluationResult> findEvaluationResultByBidAndEvaluator(int bidId, int evaluatorId);

    @Query("select avg(r.result) from EvaluationResult r where r.bid.id = ?1 and r.criteria.type = 1")
    Double getBidAverageEvaluationScore(int bidId);

    @Query("select avg(r.result) from EvaluationResult r where r.bid.id = ?1 and r.criteria.id = ?2 and r.criteria.type = 1")
    Double getBidCriteriaAverageEvaluationScore(int bidId, int criteriaId);

    @Query("select count(r) from EvaluationResult r where r.bid.id = ?1 and r.criteria.id = ?2 and r.result = ?3 and r.criteria.type = 2")
    Integer getDualCriteriaEvaluationCount(int bidId, int criteriaId, int result);
}
