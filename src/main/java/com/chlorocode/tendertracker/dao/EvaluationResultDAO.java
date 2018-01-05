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
}
