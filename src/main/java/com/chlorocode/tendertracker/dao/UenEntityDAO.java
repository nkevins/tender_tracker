package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.UenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by andy on 16/8/2017.
 */
public interface UenEntityDAO  extends JpaRepository<UenEntity, Integer> {

    @Query("select r from UenEntity r where r.uen = ?1")
    UenEntity findByUen(String uen);
}
