package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by andy on 1/8/2017.
 */
@Repository
public interface EvaluationCriteriaDAO extends JpaRepository<EvaluationCriteria, Integer> {

    @Query("select r from EvaluationCriteria r where r.tender.id = ?1")
    List<EvaluationCriteria> findEvaluationCriteriaByTender(int tenderId);
}
