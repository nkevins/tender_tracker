package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.CodeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeValueDAO extends JpaRepository<CodeValue, Integer> {

    List<CodeValue> findByTypeOrderByOrderAsc(String type);

}
