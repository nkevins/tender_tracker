package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.TenderAward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenderAwardDAO extends JpaRepository<TenderAward, Integer> {

}
