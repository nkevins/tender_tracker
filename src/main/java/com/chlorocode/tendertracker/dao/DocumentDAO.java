package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentDAO extends JpaRepository<Document, Integer> {

}
