package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Kyaw Min Thu on 3/7/2017.
 */
public interface ExternalTenderDAO extends CrudRepository<ExternalTender, Long> {
}
