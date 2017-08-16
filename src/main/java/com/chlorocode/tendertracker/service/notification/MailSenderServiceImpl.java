package com.chlorocode.tendertracker.service.notification;

import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * Kyaw Min Thu
 */
@Component
public class MailSenderServiceImpl implements MailSenderService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    public static final String OTP_SUBJECT = "[TenderTracker] Please reset your password";
    public static final String OTP_TEMPLATE = "<html><body>"
            + "<p>Dear %s,</p>"
            + "<p>Please click on following link to reset password for your account."
            + "<br><a href=\"http://localhost:8080/resetPassword/%s/%s\">\"http://localhost:8080/resetPassword/%s/%s\"</a></p>"
            + "<p>Regards,<br>Tender Tracker</p>"
            + "</body></html>";

    public static final String WELCOME_SUBJECT = "Welcome to Tender Tracker.";
    public static final String WELCOME_TEMPLATE = "<html><body>"
            + "<p>Welcome to Tender Tracker.</p>"
            + "<p>Hi %s, your account creation is completed.</p>"
            + "<p>Regards,<br>Tender Tracker</p>"
            + "</body></html>";

    public static final String UPDATE_TENDER_SUBJECT = "[TenderTracker] Tender has been updated.";
    public static final String UPDATE_TENDER_TEMPLATE = "<html><body>"
            + "<p>Tender \"%s\" has been updated.</p>"
            + "<p>Please see more information on following link."
            + "<br><a href=\"http://localhost:8080/tender/%s\">\"http://localhost:8080/tender/%s\"</a></p>"
            + "<p>Regards,<br>Tender Tracker</p>"
            + "</body></html>";

    @Override
    public boolean sendEmail(String subject, String body, String... emails) {
        // Do the business calculations...

        // Call the collaborators to persist the order...
        try {
//            MimeMessagePreparator preparator = new MimeMessagePreparator() {
//                public void prepare(MimeMessage mimeMessage) throws Exception {
//
//                    mimeMessage.setRecipients(Message.RecipientType.TO, getAddresses(emails));
//                    mimeMessage.setFrom(new InternetAddress("tendertracker.nusiss@gmail.com"));
//                    mimeMessage.setText("Dear Kyaw Min Thu, thank you for placing order. Your order number is 0001.");
//                }
//            };
//            this.mailSender.send(preparator);

            MimeMessage message = mailSender.createMimeMessage();

            // use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(emails);
            helper.setSubject(subject);

            // use the true flag to indicate the text included is HTML
            helper.setText(body, true);

            mailSender.send(message);
        } catch (Exception ex) {
            TTLogger.error(MailSenderServiceImpl.class.getName(), ex.getMessage());
            return false;
        }
        return true;
    }

    private InternetAddress[] getAddresses(List<String> emails) throws Exception {
        InternetAddress[] addresses = new InternetAddress[emails.size()];
        for (int i=0; i<emails.size(); i++) {
            addresses[i] = new InternetAddress(emails.get(i));
        }

        return addresses;
    }

}
