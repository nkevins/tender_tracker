package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by andy on 1/8/2017.
 */
@Repository
public interface EvaluationCriteriaDAO extends DataTablesRepository<Tender, Integer> {
    
}
