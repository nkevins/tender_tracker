package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.UenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by andy on 16/8/2017.
 * This DAO is used for UenEntity.
 */
public interface UenEntityDAO  extends JpaRepository<UenEntity, Integer> {

    /**
     * This method is used to find the UenEntity by uen.
     *
     * @param uen uen of the UenEntity
     * @return UenEntity
     * @see UenEntity
     */
    @Query("select r from UenEntity r where r.uen = ?1")
    UenEntity findByUen(String uen);
}
