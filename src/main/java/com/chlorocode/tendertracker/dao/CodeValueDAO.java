package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.CodeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeValueDAO extends JpaRepository<CodeValue, Integer> {

    List<CodeValue> findByTypeOrderByOrderAsc(String type);

    @Query("select c.description from CodeValue c where c.type = ?1 and c.code = ?2")
    String getDescription(String type, int code);

}
