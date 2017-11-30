package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ExternalTenderDAO;
import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ExternalTenderDAO externalTenderDAO;

    private String className;

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
}
