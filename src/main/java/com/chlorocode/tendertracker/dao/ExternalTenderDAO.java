package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Kyaw Min Thu on 3/7/2017.
 * This DAO is used for ExternalTender.
 */
public interface ExternalTenderDAO extends CrudRepository<ExternalTender, Long> {

    /**
     * This method is used to find the existing tender by refNo, title, sourceId, and companyName.
     *
     * @param refNo reference no
     * @param title title of the external tender
     * @param sourceId source id of the tender
     * @param companyName company name
     * @return ExternalTender
     * @see ExternalTender
     */
    @Query("select t from ExternalTender t where t.referenceNo = ?1 and t.title = ?2 and t.tenderSource = ?3 and t.companyName = ?4")
    ExternalTender findExistingTender(String refNo, String title, String sourceId, String companyName);

    /**
     * This method is used to close the external tender when the closingDate of such tender is less then current timestamp.
     */
    @Modifying
    @Query("update ExternalTender t set t.status = 'CLOSED', t.lastUpdatedDate = CURRENT_TIMESTAMP where t.closingDate < CURRENT_TIMESTAMP and t.status = 'OPEN'")
    void autoCloseExternalTender();
}
