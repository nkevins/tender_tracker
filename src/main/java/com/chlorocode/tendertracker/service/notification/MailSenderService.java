package com.chlorocode.tendertracker.service.notification;

/**
 * Service interface for sending email.
 */
public interface MailSenderService {

    /**
     * This method is used to send email without by direct content without using template.
     * @param subject email subject
     * @param body email body
     * @param emails receiver email address
     * @return boolean
     */
    boolean sendEmail(String subject, String body, String... emails);

    /**
     * This method is used to send email by using email template.
     *
     * @param subject email subject
     * @param templatePath email template path
     * @param emails list of receiver email address
     * @param params email template parameters
     * @return boolean
     */
    boolean sendEmail(String subject, String templatePath, String[] emails, String... params);
}
