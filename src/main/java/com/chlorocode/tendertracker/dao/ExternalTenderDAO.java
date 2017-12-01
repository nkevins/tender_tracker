package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Kyaw Min Thu on 3/7/2017.
 */
public interface ExternalTenderDAO extends CrudRepository<ExternalTender, Long> {

    @Query("select t from ExternalTender t where t.referenceNo = ?1 and t.title = ?2 and t.tenderSource = ?3 and t.companyName = ?4")
    ExternalTender findExistingTender(String refNo, String title, String sourceId, String companyName);

    @Modifying
    @Query("update ExternalTender t set t.status = 'CLOSED', t.lastUpdatedDate = CURRENT_TIMESTAMP where t.closingDate < CURRENT_TIMESTAMP and t.status = 'OPEN'")
    void autoCloseExternalTender();
}
