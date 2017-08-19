package com.chlorocode.tendertracker.service.notification;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Kyaw Min Thu
 */
@Component
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    MailSenderService mailSenderManager;

    public enum NOTI_MODE {
        welcome_message,
        tender_bookmark_noti,
        add_corrigendum_noti,
        tender_create_noti,
        company_reviewed_noti,
        reset_otp,
        decision_noti,
        // TODO add other mode such as tender_noti, etc...
        other;
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
        } else {
            sendEmail("Email Subject","Email body", (String)params.get(TTConstants.PARAM_EMAIL));
        }
    }

    public boolean sendOTP(Map<String, Object> params) {
        String name = (String) params.get(TTConstants.PARAM_NAME);
        String email = (String) params.get(TTConstants.PARAM_EMAIL);
        String token = (String) params.get(TTConstants.PARAM_TOKEN);
        String body = String.format(MailSenderServiceImpl.OTP_TEMPLATE, name, email, token, email, token);
        return sendEmail(MailSenderServiceImpl.OTP_SUBJECT, body, email);
    }

    public boolean sendWelcomeMsg(Map<String, Object> params) {
        String name = (String) params.get(TTConstants.PARAM_NAME);
        String email = (String) params.get(TTConstants.PARAM_EMAIL);
        String body = String.format(MailSenderServiceImpl.WELCOME_TEMPLATE, name);
        return sendEmail(MailSenderServiceImpl.WELCOME_SUBJECT, body, email);
    }

    public boolean  sendBookmarkNotiMsg(Map<String, Object> params) {
        String id = (String) params.get(TTConstants.PARAM_TENDER_ID);
        String title = (String) params.get(TTConstants.PARAM_TENDER_TITLE);
        int changeType = (int) params.get(TTConstants.PARAM_CHANGE_TYPE);
        String[] emails = (String[]) params.get(TTConstants.PARAM_EMAILS);
        String body = String.format(changeType == TTConstants.UPDATE_TENDER
                                    ? MailSenderServiceImpl.UPDATE_TENDER_TEMPLATE
                                    : MailSenderServiceImpl.ADD_CORRIGENDUM_TEMPLATE, title, id, id);
        sendEmail(changeType == TTConstants.UPDATE_TENDER
                    ? MailSenderServiceImpl.UPDATE_TENDER_SUBJECT
                    : MailSenderServiceImpl.ADD_CORRIGENDUM_SUBJECT, body, emails);

        return true;
    }

    public boolean  sendTenderCreateNotiMsg(Map<String, Object> params) {
        String id = (String) params.get(TTConstants.PARAM_TENDER_ID);
        String title = (String) params.get(TTConstants.PARAM_TENDER_TITLE);
        String[] emails = (String[]) params.get(TTConstants.PARAM_EMAILS);
        String body = String.format(MailSenderServiceImpl.CREATE_TENDER_TEMPLATE, title, id, id);
        sendEmail(MailSenderServiceImpl.CREATE_TENDER_SUBJECT, body, emails);

        return true;
    }

    public boolean  sendCompanyReviewedNotiMsg(Map<String, Object> params) {
        String id = (String) params.get(TTConstants.PARAM_COMPANY_ID);
        String name = (String) params.get(TTConstants.PARAM_COMPANY_NAME);
        String action = (String) params.get(TTConstants.PARAM_APPROVAL_ACTION);
        String emails = (String) params.get(TTConstants.PARAM_EMAIL);
        String body = String.format(action.equals(TTConstants.APPROVED)
                                    ? MailSenderServiceImpl.COMPANY_APPROVE_TEMPLATE
                                    : MailSenderServiceImpl.COMPANY_REJECT_TEMPLATE
                                    , name, action, id, id);
        sendEmail(MailSenderServiceImpl.COMPANY_REVIEW_SUBJECT, body, emails);

        return true;
    }

    private boolean sendEmail(String emailSubject, String emailBody, String... to) {
        return mailSenderManager.sendEmail(emailSubject, emailBody, to);
    }

}
