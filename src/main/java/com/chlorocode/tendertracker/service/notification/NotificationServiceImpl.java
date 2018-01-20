package com.chlorocode.tendertracker.service.notification;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.props.MailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kyaw Min Thu
 */
@Component
@EnableConfigurationProperties(MailProperties.class)
public class NotificationServiceImpl implements NotificationService {

    private MailSenderService mailSenderManager;

    private MailProperties mailProperties;

    public enum NOTI_MODE {
        welcome_message,
        tender_bookmark_noti,
        add_corrigendum_noti,
        tender_create_noti,
        company_reviewed_noti,
        reset_otp,
        decision_noti,
        company_reg_noti,
        tender_closed_noti,

        company_blacklisted_noti,

        milestone_approach_noti,
        appeal_create_noti,
        appeal_update_noti,

        tender_award_noti,

        // TODO add other mode such as tender_noti, etc...
        other;
    }

    @Autowired
    public NotificationServiceImpl (MailSenderService mailSenderManager, MailProperties mailProperties) {
        this.mailSenderManager = mailSenderManager;
        this.mailProperties = mailProperties;
    }

    @Async
    public void sendNotification(NOTI_MODE mode, Map<String, Object> params) {
        if (mode == NOTI_MODE.reset_otp) {
            sendOTP(params);
        } else if (mode == NOTI_MODE.welcome_message) {
            sendWelcomeMsg(params);
        } else if (mode == NOTI_MODE.tender_bookmark_noti) {
            sendBookmarkNotiMsg(params);
        } else if (mode == NOTI_MODE.tender_create_noti) {
            sendTenderCreateNotiMsg(params);
        } else if (mode == NOTI_MODE.company_reviewed_noti) {
            sendCompanyReviewedNotiMsg(params);
        } else if (mode == NOTI_MODE.company_reg_noti) {
            sendCompanyRegisteredNotiMsg(params);
        } else if (mode == NOTI_MODE.tender_closed_noti) {
            sendTenderClosedNotiMsg(params);
        } else if (mode == NOTI_MODE.company_blacklisted_noti) {
            sendCompanyBlackListedNotiMsg(params);
        } else if (mode == NOTI_MODE.milestone_approach_noti) {
            sendMilestoneApproachNotiMsg(params);
        } else if (mode == NOTI_MODE.appeal_create_noti) {
            sendAppealCreateNotiMsg(params);
        } else if (mode == NOTI_MODE.appeal_update_noti) {
            sendAppealUpdateNotiMsg(params);
        } else if (mode == NOTI_MODE.tender_award_noti) {
            sendTenderAwardNotiMsg(params);
        } else {
            //sendEmail("Email Subject","Email body", (String)params.get(TTConstants.PARAM_EMAIL));
        }
    }

    public boolean sendOTP(Map<String, Object> params) {
        String name = (String) params.get(TTConstants.PARAM_NAME);
        String email = (String) params.get(TTConstants.PARAM_EMAIL);
        String token = (String) params.get(TTConstants.PARAM_TOKEN);
        return sendEmail(mailProperties.getSubOTP(), mailProperties.getTemplateOTP(), new String[]{email}, name, email, token, email, token);
    }

    public boolean sendWelcomeMsg(Map<String, Object> params) {
        String name = (String) params.get(TTConstants.PARAM_NAME);
        String email = (String) params.get(TTConstants.PARAM_EMAIL);
        return sendEmail(mailProperties.getSubWelcome(), mailProperties.getTemplateWelcome(), new String[]{email}, name);
    }

    public boolean  sendBookmarkNotiMsg(Map<String, Object> params) {
        String id = String.valueOf(params.get(TTConstants.PARAM_TENDER_ID));
        String title = (String) params.get(TTConstants.PARAM_TENDER_TITLE);
        int changeType = (int) params.get(TTConstants.PARAM_CHANGE_TYPE);
        String[] emails = (String[]) params.get(TTConstants.PARAM_EMAILS);
        if (changeType == TTConstants.UPDATE_TENDER) {
            return sendEmail(mailProperties.getSubUpdateTender(), mailProperties.getTemplateUpdateTender(), emails, title, id, id);
        } else {
            return sendEmail(mailProperties.getSubAddCorrigendum(), mailProperties.getTemplateAddCorrigendum(), emails, title, id, id);
        }
    }

    public boolean  sendTenderCreateNotiMsg(Map<String, Object> params) {
        String id = String.valueOf(params.get(TTConstants.PARAM_TENDER_ID));
        String title = (String) params.get(TTConstants.PARAM_TENDER_TITLE);
        String[] emails = (String[]) params.get(TTConstants.PARAM_EMAILS);
        return sendEmail(mailProperties.getSubCreateTender(), mailProperties.getTemplateCreateTender(), emails, title, id, id);
    }

    public boolean  sendTenderClosedNotiMsg(Map<String, Object> params) {
        String id = String.valueOf(params.get(TTConstants.PARAM_TENDER_ID));
        String title = (String) params.get(TTConstants.PARAM_TENDER_TITLE);
        String[] emails = (String[]) params.get(TTConstants.PARAM_EMAILS);
        return sendEmail(mailProperties.getSubClosedTender(), mailProperties.getTemplateClosedTender()
                , emails, title, id, id);
    }

    public boolean  sendTenderAwardNotiMsg(Map<String, Object> params) {
        String id = String.valueOf(params.get(TTConstants.PARAM_TENDER_ID));
        String title = (String) params.get(TTConstants.PARAM_TENDER_TITLE);
        String companyName = (String) params.get(TTConstants.PARAM_COMPANY_NAME);
        String[] emails = (String[]) params.get(TTConstants.PARAM_EMAILS);
        return sendEmail(mailProperties.getSubTenderAwarded(), mailProperties.getTemplateTenderAwarded()
                , emails, companyName, title, id, id);
    }

    public boolean  sendCompanyReviewedNotiMsg(Map<String, Object> params) {
        String id = String.valueOf(params.get(TTConstants.PARAM_COMPANY_ID));
        String name = (String) params.get(TTConstants.PARAM_COMPANY_NAME);
        String action = (String) params.get(TTConstants.PARAM_APPROVAL_ACTION);
        String email = (String) params.get(TTConstants.PARAM_EMAIL);
        return sendEmail(mailProperties.getSubCompanyReview()
                            , action.equals(TTConstants.APPROVED)
                                ? mailProperties.getTemplateCompanyApproved() : mailProperties.getTemplateCompanyRejected()
                            , new String[]{email}, name, action, id, id);
    }

    public boolean sendCompanyRegisteredNotiMsg(Map<String, Object> params) {
        Company company = (Company) params.get(TTConstants.PARAM_COMPANY);
        String email = (String) params.get(TTConstants.PARAM_EMAIL);
        return sendEmail(mailProperties.getSubCompanyRegistered(), mailProperties.getTemplateCompanyRegistered(),
                new String[]{email}, company.getName());
    }


    public boolean sendCompanyBlackListedNotiMsg(Map<String, Object> params) {
        Company company = (Company) params.get(TTConstants.PARAM_COMPANY);
        String[] emails = (String[]) params.get(TTConstants.PARAM_EMAILS);
        return sendEmail(mailProperties.getSubCompanyBlacklisted(), mailProperties.getTemplateCompanyBlacklisted(),
                emails, company.getName());
    }

    public boolean  sendMilestoneApproachNotiMsg(Map<String, Object> params) {
        String id = String.valueOf(params.get(TTConstants.PARAM_TENDER_ID));
        String title = (String) params.get(TTConstants.PARAM_TENDER_TITLE);
        String description = (String) params.get(TTConstants.PARAM_MILESTONE_DESCRIPTION);
        String dueDate = (String) params.get(TTConstants.PARAM_MILESTONE_DUE_DATE);
        String status = (String) params.get(TTConstants.PARAM_MILESTONE_STATUS);
        String[] emails = (String[]) params.get(TTConstants.PARAM_EMAILS);
        return sendEmail(mailProperties.getSubMilestoneApproach()
                , mailProperties.getTemplateMilestoneApproach()
                , emails, description, title, dueDate, status, id, id);
    }

    public boolean  sendAppealCreateNotiMsg(Map<String, Object> params) {
        String id = String.valueOf(params.get(TTConstants.PARAM_TENDER_ID));
        String title = (String) params.get(TTConstants.PARAM_TENDER_TITLE);
        String appealCompany = (String) params.get(TTConstants.PARAM_APPEAL_COMPANY);
        String[] emails = (String[]) params.get(TTConstants.PARAM_EMAILS);
        return sendEmail(mailProperties.getSubAppealCreate(), mailProperties.getTemplateAppealCreate(), emails, appealCompany, title, id, id);
    }

    public boolean  sendAppealUpdateNotiMsg(Map<String, Object> params) {
        String id = String.valueOf(params.get(TTConstants.PARAM_TENDER_ID));
        String title = (String) params.get(TTConstants.PARAM_TENDER_TITLE);
        String appealCompany = (String) params.get(TTConstants.PARAM_APPEAL_COMPANY);
        int action = (int) params.get(TTConstants.PARAM_APPEAL_ACTION);
        String[] emails = (String[]) params.get(TTConstants.PARAM_EMAILS);
        if (action == TTConstants.APPEAL_ACCEPT) {
            return sendEmail(mailProperties.getSubAppealAccepted(), mailProperties.getTemplateAppealAccepted(), emails, appealCompany, title, id, id);
        } else {
            return sendEmail(mailProperties.getSubAppealRejected(), mailProperties.getTemplateAppealRejected(), emails, appealCompany, title, id, id);
        }
    }

    private boolean sendEmail(String emailSubject, String path, String[] to, String... params) {
        return mailSenderManager.sendEmail(emailSubject, path, to, params);
    }

}
