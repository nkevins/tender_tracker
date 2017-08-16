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
        bookmark_noti,
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
        } else if (mode == NOTI_MODE.bookmark_noti) {
            sendBookmarkNotiMsg(params);
        } else {
            sendEmail("Email Subject","Email body", (String)params.get(TTConstants.PARAM_EMAIL));
        }
    }

    public boolean sendOTP(Map<String, Object> params) {
        String body = String.format(MailSenderServiceImpl.OTP_TEMPLATE
                                    , params.get(TTConstants.PARAM_NAME)
                                    , params.get(TTConstants.PARAM_EMAIL)
                                    , params.get(TTConstants.PARAM_TOKEN)
                                    , params.get(TTConstants.PARAM_EMAIL)
                                    , params.get(TTConstants.PARAM_TOKEN));
        return sendEmail(MailSenderServiceImpl.OTP_SUBJECT, body, (String)params.get(TTConstants.PARAM_EMAIL));
    }

    public boolean sendWelcomeMsg(Map<String, Object> params) {
        String body = String.format(MailSenderServiceImpl.WELCOME_TEMPLATE, params.get(TTConstants.PARAM_NAME));
        return sendEmail(MailSenderServiceImpl.WELCOME_SUBJECT, body, (String)params.get(TTConstants.PARAM_EMAIL));
    }

    public boolean  sendBookmarkNotiMsg(Map<String, Object> params) {
        Tender tender = (Tender) params.get(TTConstants.PARAM_TENDER);
        String body = String.format(MailSenderServiceImpl.UPDATE_TENDER_TEMPLATE, tender.getTitle(), tender.getId(), tender.getId());
        sendEmail(MailSenderServiceImpl.UPDATE_TENDER_SUBJECT, body, (String[])params.get(TTConstants.PARAM_EMAILS));

        return true;
    }

    private boolean sendEmail(String emailSubject, String emailBody, String... to) {
        return mailSenderManager.sendEmail(emailSubject, emailBody, to);
    }

}
