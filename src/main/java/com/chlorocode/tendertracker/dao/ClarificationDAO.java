package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by andy on 8/8/2017.
 */
@Repository
public interface ClarificationDAO extends JpaRepository<Clarification, Integer> {

    @Query("select r from Clarification r where r.tender.id = ?1")
    List<Clarification> findClarificationByTenderId(int tenderId);

}
