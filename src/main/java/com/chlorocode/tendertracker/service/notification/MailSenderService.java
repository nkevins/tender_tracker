package com.chlorocode.tendertracker.service.notification;

import java.util.List;

/**
 * Kyaw Min Thu
 */
public interface MailSenderService {
    public boolean sendEmail(String subject, String body, String... emails);
}
