package com.chlorocode.tendertracker.service.notification;

import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Kyaw Min Thu
 */
@Component
public class MailSenderServiceImpl implements MailSenderService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Value("${mail.from.account}")
    private String fromAccount;

    @Override
    public boolean sendEmail(String subject, String body, String... emails) {
        // Do the business calculations...
        // Call the collaborators to persist the order...
        try {
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

    @Override
    public boolean sendEmail(String subject, String templatePath, String[] emails, String... params) {
        // Do the business calculations...
        // Call the collaborators to persist the order...
        try {
            MimeMessage message = mailSender.createMimeMessage();

            // use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromAccount);
            helper.setTo(emails);
            helper.setSubject(subject);

            // use the true flag to indicate the text included is HTML
            helper.setText(getEmailBody(templatePath, params), true);

            mailSender.send(message);
        } catch (Exception ex) {
            TTLogger.error(MailSenderServiceImpl.class.getName(), ex.getMessage());
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private String getEmailBody(String templatePath, String... params) throws Exception {
        InputStream is = new ClassPathResource(templatePath).getInputStream();
        Scanner s = new Scanner(is).useDelimiter("\\A");
        String content = s.hasNext() ? s.next() : "";

        if (params.length > 0) {
            for (int i=0; i<params.length; i++) {
                content = content.replaceFirst("PARAM"+i, params[i]);
            }
        }
        return content;
    }

    private InternetAddress[] getAddresses(List<String> emails) throws Exception {
        InternetAddress[] addresses = new InternetAddress[emails.size()];
        for (int i=0; i<emails.size(); i++) {
            addresses[i] = new InternetAddress(emails.get(i));
        }

        return addresses;
    }

}
