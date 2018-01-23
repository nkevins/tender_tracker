package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.*;
import com.chlorocode.tendertracker.dao.dto.TenderSearchDTO;
import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.specs.ExternalTenderSpecs;
import com.chlorocode.tendertracker.dao.specs.TenderSpecs;
import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.List;

/**
 * Created by Kyaw Min Thu on 5/1/2017.
 */
@Service
public class ExternalTenderServiceImpl implements ExternalTenderService {

    private ExternalTenderDAO externalTenderDAO;
    private ExternalTenderPagingDAO externalTenderPagingDAO;

    private String className;

    @Autowired
    public ExternalTenderServiceImpl(ExternalTenderDAO externalTenderDAO, ExternalTenderPagingDAO externalTenderPagingDAO) {
        this.externalTenderDAO = externalTenderDAO;
        this.externalTenderPagingDAO = externalTenderPagingDAO;
    }

    @PostConstruct
    public void postConstruct() {
        className = this.getClass().getName();
        TTLogger.debug(className, "**** TenderWCServiceImpl postConstruct() ****");
    }

    @PreDestroy
    public void preDestroy() {
        TTLogger.debug(className, "---- TenderWCServiceImpl preDestroy() ----");
    }

    @Override
    public String createTenderWCList(List<ExternalTender> tenders) {
        if (tenders != null) {
            for (ExternalTender tender : tenders) {
                try {
                    ExternalTender externalTender = externalTenderDAO.findExistingTender(tender.getReferenceNo(),
                            tender.getTitle(), tender.getTenderSource(), tender.getCompanyName());

                    if (externalTender == null) {
                        TTLogger.debug(className, "createTenderWC(TenderWC=%s)", tender);

                        tender.setCreatedDate(new Date());
                        tender.setLastUpdatedDate(new Date());
                        externalTenderDAO.save(tender);

                        TTLogger.debug(className, "created TenderWC(TenderWC=%s)", tender);
                    } else {
                        TTLogger.debug(className, "updateTenderWC(TenderWC=%s)", tender);

                        externalTender.setPublishedDate(tender.getPublishedDate());
                        externalTender.setClosingDate(tender.getClosingDate());
                        externalTender.setStatus(tender.getStatus());
                        externalTender.setTenderURL(tender.getTenderURL());
                        externalTender.setLastUpdatedDate(new Date());

                        externalTenderDAO.save(externalTender);

                        TTLogger.debug(className, "updated TenderWC(TenderWC=%s)", tender);
                    }
                } catch (Exception e) {
                    TTLogger.error(className, "Error while createTenderWC(tenderWC=" + tender.toString(), e);
                }
            }
        }
        return "success";
    }

    @Override
    public Page<ExternalTender> listAllByPage(Pageable pageable) {
        Specification<ExternalTender> searchSpec = ExternalTenderSpecs.getAllOpenTender();
        return externalTenderPagingDAO.findAll(searchSpec, pageable);
    }

    @Override
    public Page<ExternalTender> searchTender(TenderSearchDTO searchDTO, Pageable pageable) {
        Specification<ExternalTender> searchSpec = null;
        if (searchDTO.getSearchText() != null && !searchDTO.getSearchText().trim().isEmpty()) {
            searchSpec = ExternalTenderSpecs.byTenderSearchString(searchDTO.getSearchText().trim());
            searchDTO.setCompanyName(null);
            searchDTO.setTitle(null);
            searchDTO.setRefNo(null);
            searchDTO.setEtStatus(null);
        } else {
            searchSpec = ExternalTenderSpecs.byTenderSearchCriteria(
                    searchDTO.getTitle() == null ? null : searchDTO.getTitle().trim()
                    , searchDTO.getCompanyName() == null ? null : searchDTO.getCompanyName().trim()
                    , searchDTO.getEtStatus(), searchDTO.getTenderSource(), searchDTO.getRefNo());
            searchDTO.setSearchText(null);
        }
        return externalTenderPagingDAO.findAll(searchSpec, pageable);
    }

    @Override
    public ExternalTender findByID(int id) {
        return externalTenderDAO.findOne((long)id);
    }
}
