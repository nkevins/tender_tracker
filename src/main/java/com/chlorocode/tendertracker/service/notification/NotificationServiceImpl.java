package com.chlorocode.tendertracker.service.notification;

import com.chlorocode.tendertracker.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Kyaw Min Thu
 */
@Component
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    MailSenderService mailSenderManager;

    public enum NOTI_MODE {
        reset_otp,
        // TODO add other mode such as tender_noti, etc...
        other;
    }

    @Override
    public boolean sendNotification(NOTI_MODE mode,  User... to) {
        boolean result = false;
        if (mode == NOTI_MODE.reset_otp) {
            result = sendOTP(to[0]);
        } else {
            result = sendEmail("Email body", to);
        }
        return result;
    }

    private boolean sendOTP(User u) {
        return sendEmail(String.format(MailSenderServiceImpl.OTP_TEMPLATE
                                , u.getName(), u.getEmail(), u.getConfirmationToken()
                                , u.getEmail(), u.getConfirmationToken()), u);
    }

    private boolean sendEmail(String emailBody, User... to) {
        List<String> emails = new ArrayList<>();
        for (User each : to) {
            emails.add(each.getEmail());
        }
        return mailSenderManager.sendEmail(emailBody, emails);
    }

}
