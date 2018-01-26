package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.CodeValue;
import com.chlorocode.tendertracker.dao.entity.Country;
import com.chlorocode.tendertracker.dao.entity.TenderCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This DAO is used for finding predefined status, constant and combo box values(CodeValue).
 */
@Repository
public interface CodeValueDAO extends JpaRepository<CodeValue, Integer> {

    /**
     * This method is used to find the list of code value by type.
     *
     * @param type type of the constant/status
     * @return List
     * @see CodeValue
     */
    List<CodeValue> findByTypeOrderByOrderAsc(String type);

    /**
     * This method is used to get the description of CodeValue object.
     *
     * @param type type of the constant/status
     * @param code code of the constant/status
     * @return String
     * @see CodeValue
     */
    @Query("select c.description from CodeValue c where c.type = ?1 and c.code = ?2")
    String getDescription(String type, int code);

}
