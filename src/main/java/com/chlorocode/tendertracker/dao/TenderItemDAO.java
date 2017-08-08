package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.TenderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenderItemDAO extends JpaRepository<TenderItem, Integer> {

}
