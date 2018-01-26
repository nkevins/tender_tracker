package com.chlorocode.tendertracker.scheduler;

import com.chlorocode.tendertracker.dao.ExternalTenderDAO;
import com.chlorocode.tendertracker.dao.TenderDAO;
import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.MilestoneService;
import com.chlorocode.tendertracker.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * This class is used to configure all the spring scheduled task.
 */
@Component
public class TenderScheduledTask {

    private String className;
    private ExternalTenderDAO externalTenderDAO;
    private TenderDAO tenderDAO;
    private TenderService tenderService;
    private MilestoneService milestoneService;

    /**
     * Constructor
     *
     * @param externalTenderDAO ExternalTenderDAO
     * @param tenderDAO TenderDAO
     * @param tenderService TenderService
     * @param milestoneService MilestoneService
     */
    @Autowired
    public TenderScheduledTask(ExternalTenderDAO externalTenderDAO, TenderDAO tenderDAO, TenderService tenderService, MilestoneService milestoneService) {
        this.className = this.getClass().getName();
        this.externalTenderDAO = externalTenderDAO;
        this.tenderDAO = tenderDAO;
        this.tenderService = tenderService;
        this.milestoneService = milestoneService;
    }

    /**
     * This is the job to auto closed expired tender.
     */
    @Scheduled(cron="0 0 3 * * *", zone = "Asia/Singapore") // Daily every 3 AM Singapore Time
    @Transactional
    public void tenderAutoClose() {
        TTLogger.info(className, "Auto Close status update for External Tender");
        externalTenderDAO.autoCloseExternalTender();
        TTLogger.info(className, "Completed Auto Close status update for External Tender");

        TTLogger.info(className, "Auto Close status update for Tender");
        tenderDAO.autoCloseTender();
        TTLogger.info(className, "Completed Auto Close status update for Tender");
    }

    /**
     * This is the job to send email notification to company administrator that tender has been closed and ready for evaluation.
     */
    @Scheduled(cron="0 * * * * *", zone = "Asia/Singapore") // Will work at the start of every minute.
    @Transactional
    public void autoNotifyTenderClose() {
        TTLogger.info(className, "Auto notify tender close.");
        //Notify company administrator that the tender has closed and ready for evaluation.");
        tenderService.autoCloseTenderAndNotify();
        TTLogger.info(className, "Completed tender close and notify.");
    }

    /**
     * This is the job to notify company administrator that the tender milestone is approaching.
     */
    @Scheduled(cron="0 * * * * *", zone = "Asia/Singapore") // Will work at the start of every minute.
    @Transactional
    public void autoNotifyApproachMilestone() {
        TTLogger.info(className, "Auto notify approach milestone.");
        //Notify company administrator that the milestone of the company tender has been approaching.");
        milestoneService.notifyApproachMilestone();
        TTLogger.info(className, "Completed approach milestone notify.");
    }
}
