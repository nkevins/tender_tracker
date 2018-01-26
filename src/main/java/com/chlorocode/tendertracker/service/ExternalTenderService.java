package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.dto.TenderSearchDTO;
import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for external tender.
 */
public interface ExternalTenderService {

    /**
     * This method is used to save external tender passed from tender crawler via API.
     *
     * @param tenders list of ExternalTender to be saved
     * @return String
     */
    String createTenderWCList(List<ExternalTender> tenders);

    /**
     * This method is used to get all external tenders.
     *
     * @param pageable paging criteria
     * @return page of ExternalTender
     */
    Page<ExternalTender> listAllByPage(Pageable pageable);

    /**
     * This method is used to search external tender.
     *
     * @param searchDTO search criteria
     * @param pageable paging criteria
     * @return page of ExternalTender
     * @see TenderSearchDTO
     */
    Page<ExternalTender> searchTender(TenderSearchDTO searchDTO, Pageable pageable);

    /**
     * This method is used to get an external tender by id.
     *
     * @param id unique identifier of the external tender
     * @return ExternalTender
     */
    ExternalTender findByID(int id);
}
