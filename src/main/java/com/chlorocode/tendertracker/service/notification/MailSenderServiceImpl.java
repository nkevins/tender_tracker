package com.chlorocode.tendertracker.service.notification;

import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Kyaw Min Thu
 */
@Component
public class MailSenderServiceImpl implements MailSenderService {

    @Autowired
    private JavaMailSenderImpl mailSender;

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

    @Override
    public boolean sendEmail(String subject, String templatePath, String[] emails, String... params) {
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
            helper.setText(getEmailBody(templatePath, params), true);

            mailSender.send(message);
        } catch (Exception ex) {
            TTLogger.error(MailSenderServiceImpl.class.getName(), ex.getMessage());
            return false;
        }
        return true;
    }

    private String getEmailBody(String templatePath, String... params) {
        try {
            File file = new File(templatePath);
            FileReader reader = null;
                reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            String content = new String(chars);

            if (params.length > 0) {
                for (int i=0; i<params.length; i++) {
                    content = content.replaceFirst("PARAM"+i, params[i]);
                }
            }
            return content;
        } catch (FileNotFoundException e) {
            TTLogger.error(MailSenderServiceImpl.class.getName(), "Exception", e);
        } catch (IOException e) {
            TTLogger.error(MailSenderServiceImpl.class.getName(), "Exception", e);
        }
        return null;
    }

    private InternetAddress[] getAddresses(List<String> emails) throws Exception {
        InternetAddress[] addresses = new InternetAddress[emails.size()];
        for (int i=0; i<emails.size(); i++) {
            addresses[i] = new InternetAddress(emails.get(i));
        }

        return addresses;
    }

}
