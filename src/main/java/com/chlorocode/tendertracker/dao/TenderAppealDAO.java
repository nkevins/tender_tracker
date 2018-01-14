package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.TenderAppeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by andy on 14/1/2018.
 */
@Repository
public interface TenderAppealDAO extends JpaRepository<TenderAppeal, Integer>  {

}
