package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.CompanyRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRegistrationDAO extends JpaRepository<CompanyRegistration, Integer> {

}
