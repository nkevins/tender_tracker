package com.chlorocode.tendertracker.service.notification;

import java.util.List;

/**
 * Kyaw Min Thu
 */
public interface MailSenderService {
    public boolean sendEmail(String body, List<String> emails);
}
