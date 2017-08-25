package com.chlorocode.tendertracker.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mail")
public class MailProperties {

    private String subOTP;
    private String subWelcome;
    private String subUpdateTender;
    private String subAddCorrigendum;
    private String subCreateTender;
    private String subCompanyReview;
    private String subCompanyRegistered;
    private String templateOTP;
    private String templateWelcome;
    private String templateUpdateTender;
    private String templateAddCorrigendum;
    private String templateCreateTender;
    private String templateCompanyApproved;
    private String templateCompanyRejected;
    private String templateCompanyRegistered;

    public String getSubOTP() {
        return subOTP;
    }

    public void setSubOTP(String subOTP) {
        this.subOTP = subOTP;
    }

    public String getSubWelcome() {
        return subWelcome;
    }

    public void setSubWelcome(String subWelcome) {
        this.subWelcome = subWelcome;
    }

    public String getSubUpdateTender() {
        return subUpdateTender;
    }

    public void setSubUpdateTender(String subUpdateTender) {
        this.subUpdateTender = subUpdateTender;
    }

    public String getSubAddCorrigendum() {
        return subAddCorrigendum;
    }

    public void setSubAddCorrigendum(String subAddCorrigendum) {
        this.subAddCorrigendum = subAddCorrigendum;
    }

    public String getSubCreateTender() {
        return subCreateTender;
    }

    public void setSubCreateTender(String subCreateTender) {
        this.subCreateTender = subCreateTender;
    }

    public String getSubCompanyReview() {
        return subCompanyReview;
    }

    public void setSubCompanyReview(String subCompanyReview) {
        this.subCompanyReview = subCompanyReview;
    }

    public String getSubCompanyRegistered() {
        return subCompanyRegistered;
    }

    public void setSubCompanyRegistered(String subCompanyRegistered) {
        this.subCompanyRegistered = subCompanyRegistered;
    }

    public String getTemplateOTP() {
        return templateOTP;
    }

    public void setTemplateOTP(String templateOTP) {
        this.templateOTP = templateOTP;
    }

    public String getTemplateWelcome() {
        return templateWelcome;
    }

    public void setTemplateWelcome(String templateWelcome) {
        this.templateWelcome = templateWelcome;
    }

    public String getTemplateUpdateTender() {
        return templateUpdateTender;
    }

    public void setTemplateUpdateTender(String templateUpdateTender) {
        this.templateUpdateTender = templateUpdateTender;
    }

    public String getTemplateAddCorrigendum() {
        return templateAddCorrigendum;
    }

    public void setTemplateAddCorrigendum(String templateAddCorrigendum) {
        this.templateAddCorrigendum = templateAddCorrigendum;
    }

    public String getTemplateCreateTender() {
        return templateCreateTender;
    }

    public void setTemplateCreateTender(String templateCreateTender) {
        this.templateCreateTender = templateCreateTender;
    }

    public String getTemplateCompanyApproved() {
        return templateCompanyApproved;
    }

    public void setTemplateCompanyApproved(String templateCompanyApproved) {
        this.templateCompanyApproved = templateCompanyApproved;
    }

    public String getTemplateCompanyRejected() {
        return templateCompanyRejected;
    }

    public void setTemplateCompanyRejected(String templateCompanyRejected) {
        this.templateCompanyRejected = templateCompanyRejected;
    }

    public String getTemplateCompanyRegistered() {
        return templateCompanyRegistered;
    }

    public void setTemplateCompanyRegistered(String templateCompanyRegistered) {
        this.templateCompanyRegistered = templateCompanyRegistered;
    }
    //    private String subjectOTP;
//    public static final String OTP_TEMPLATE = "<html><body>"
//            + "<p>Dear %s,</p>"
//            + "<p>Please click on following link to reset password for your account."
//            + "<br><a href=\"http://localhost:8080/resetPassword/%s/%s\">\"http://localhost:8080/resetPassword/%s/%s\"</a></p>"
//            + "<p>Regards,<br>Tender Tracker</p>"
//            + "</body></html>";
//
//    @Value("${subject.welcome_mail}")
//    private String subjectWelcome;
//    public static final String WELCOME_TEMPLATE = "<html><body>"
//            + "<p>Welcome to Tender Tracker.</p>"
//            + "<p>Hi %s, your account creation is completed.</p>"
//            + "<p>Regards,<br>Tender Tracker</p>"
//            + "</body></html>";
//
//    @Value("${subject.update_tender_mail}")
//    private String subjectUpdateTender;
//    public static final String UPDATE_TENDER_TEMPLATE = "<html><body>"
//            + "<p>Tender \"%s\" has been updated.</p>"
//            + "<p>Please see more information on following link."
//            + "<br><a href=\"http://localhost:8080/tender/%s\">\"http://localhost:8080/tender/%s\"</a></p>"
//            + "<p>Regards,<br>Tender Tracker</p>"
//            + "</body></html>";
//
//    @Value("${subject.add_corrigendum_mail}")
//    private String subjectAddCorrigendum;
//    public static final String ADD_CORRIGENDUM_TEMPLATE = "<html><body>"
//            + "<p>New corrigendum add into the tender \"%s\".</p>"
//            + "<p>Please see more information on following link."
//            + "<br><a href=\"http://localhost:8080/tender/%s\">\"http://localhost:8080/tender/%s\"</a></p>"
//            + "<p>Regards,<br>Tender Tracker</p>"
//            + "</body></html>";
//
//    @Value("${subject.create_tender_mail}")
//    private String subjectCreateTender;
//    public static final String CREATE_TENDER_TEMPLATE = "<html><body>"
//            + "<p>Tender \"%s\" has been created.</p>"
//            + "<p>Please see more information on following link."
//            + "<br><a href=\"http://localhost:8080/tender/%s\">\"http://localhost:8080/tender/%s\"</a></p>"
//            + "<p>Regards,<br>Tender Tracker</p>"
//            + "</body></html>";
//
//    @Value("${subject.company_review_mail}")
//    private String subjectCompanyReview;
//    public static final String COMPANY_APPROVE_TEMPLATE = "<html><body>"
//            + "<p>Congratulation, your company \"%s\" registration request has been approved.</p>"
//            + "<p>Regards,<br>Tender Tracker</p>"
//            + "</body></html>";
//    public static final String COMPANY_REJECT_TEMPLATE = "<html><body>"
//            + "<p>Sorry, your company \"%s\" registration request has been rejected.</p>"
//            + "<p>Regards,<br>Tender Tracker</p>"
//            + "</body></html>";
}
