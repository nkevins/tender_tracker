package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyDAO extends JpaRepository<Company, Integer> {

}
