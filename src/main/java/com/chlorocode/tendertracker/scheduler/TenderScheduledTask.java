package com.chlorocode.tendertracker.scheduler;

import com.chlorocode.tendertracker.dao.ExternalTenderDAO;
import com.chlorocode.tendertracker.dao.TenderDAO;
import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class TenderScheduledTask {

    private String className;
    private ExternalTenderDAO externalTenderDAO;
    private TenderDAO tenderDAO;

    @Autowired
    public TenderScheduledTask(ExternalTenderDAO externalTenderDAO, TenderDAO tenderDAO) {
        this.className = this.getClass().getName();
        this.externalTenderDAO = externalTenderDAO;
        this.tenderDAO = tenderDAO;
    }

    @Scheduled(cron="0 0 3 * * *", zone = "Asia/Singapore")
    @Transactional
    public void tenderAutoClose() {
        TTLogger.info(className, "Auto Close status update for External Tender");
        externalTenderDAO.autoCloseExternalTender();
        TTLogger.info(className, "Completed Auto Close status update for External Tender");

        TTLogger.info(className, "Auto Close status update for Tender");
        tenderDAO.autoCloseTender();
        TTLogger.info(className, "Completed Auto Close status update for Tender");
    }
}
