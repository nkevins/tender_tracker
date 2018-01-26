package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.CodeValue;
import com.chlorocode.tendertracker.dao.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This DAO is used for Country.
 */
@Repository
public interface CountryDAO extends JpaRepository<Country, Integer> {

    /**
     * This method is used to get all countries.
     *
     * @return List
     * @see Country
     */
    @Query("select c from Country c order by c.name")
    List<Country> getAllCountries();

}
