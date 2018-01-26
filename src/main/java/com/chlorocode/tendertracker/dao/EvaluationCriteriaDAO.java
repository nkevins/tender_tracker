package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by andy on 1/8/2017.
 * This DAO used for EvaluationCriteria.
 */
@Repository
public interface EvaluationCriteriaDAO extends JpaRepository<EvaluationCriteria, Integer> {
    /**
     * This method is used to find the EvaluationCriteria by tenderId.
     *
     * @param tenderId unique identifier of the tender
     * @return List
     * @see EvaluationCriteria
     */
    @Query("select r from EvaluationCriteria r where r.tender.id = ?1")
    List<EvaluationCriteria> findEvaluationCriteriaByTender(int tenderId);
}
