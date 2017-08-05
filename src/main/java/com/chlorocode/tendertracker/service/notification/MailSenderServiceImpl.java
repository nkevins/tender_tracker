package com.chlorocode.tendertracker.service.notification;

import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Kyaw Min Thu
 */
@Component
public class MailSenderServiceImpl implements MailSenderService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    public static final String OTP_TEMPLATE = "<html><body>"
            + "<p>Dear %s,</p>"
            + "<p>Please click on following link to reset password for your account."
            + "<br><a href=\"http://localhost:8080/resetPassword/%s/%s\">\"http://localhost:8080/resetPassword/%s/%s\"</a></p>"
            + "<p>Regards,<br>Tender Tracker</p>"
            + "</body></html>";

    @Override
    public boolean sendEmail(String body, List<String> emails) {
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
            helper.setTo(emails.toArray(new String[emails.size()]));

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
