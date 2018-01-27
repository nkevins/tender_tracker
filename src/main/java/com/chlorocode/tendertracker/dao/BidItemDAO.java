package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.BidDocument;
import com.chlorocode.tendertracker.dao.entity.BidItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by andy on 27/1/2018.
 */
public interface BidItemDAO extends JpaRepository<BidItem, Integer> {

}
