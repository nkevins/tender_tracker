package com.chlorocode.tendertracker.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This class is used to get mail properties value.
 */
@ConfigurationProperties(prefix = "mail")
public class MailProperties {

    private String subOTP;
    private String subWelcome;
    private String subUpdateTender;
    private String subAddCorrigendum;
    private String subCreateTender;
    private String subClosedTender;
    private String subCompanyReview;
    private String subCompanyRegistered;
    private String subTenderAwarded;

    private String subCompanyBlacklisted;

    private String subMilestoneApproach;
    private String subAppealCreate;
    private String subAppealUpdate;
    private String subAppealAccepted;
    private String subAppealRejected;

    private String templateOTP;
    private String templateWelcome;
    private String templateUpdateTender;
    private String templateAddCorrigendum;
    private String templateCreateTender;
    private String templateClosedTender;
    private String templateCompanyApproved;
    private String templateCompanyRejected;
    private String templateCompanyRegistered;
    private String templateTenderAwarded;

    private String templateCompanyBlacklisted;

    private String templateMilestoneApproach;
    private String templateAppealCreate;
    private String templateAppealUpdate;
    private String templateAppealAccepted;
    private String templateAppealRejected;


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

    public String getSubClosedTender() {
        return subClosedTender;
    }

    public void setSubClosedTender(String subClosedTender) {
        this.subClosedTender = subClosedTender;
    }

    public String getTemplateClosedTender() {
        return templateClosedTender;
    }

    public void setTemplateClosedTender(String templateClosedTender) {
        this.templateClosedTender = templateClosedTender;
    }


    public String getSubCompanyBlacklisted() {
        return subCompanyBlacklisted;
    }

    public void setSubCompanyBlacklisted(String subCompanyBlacklisted) {
        this.subCompanyBlacklisted = subCompanyBlacklisted;
    }

    public String getTemplateCompanyBlacklisted() {
        return templateCompanyBlacklisted;
    }

    public void setTemplateCompanyBlacklisted(String templateCompanyBlacklisted) {
        this.templateCompanyBlacklisted = templateCompanyBlacklisted;
    }

    public String getSubMilestoneApproach() {
        return subMilestoneApproach;
    }

    public void setSubMilestoneApproach(String subMilestoneApproach) {
        this.subMilestoneApproach = subMilestoneApproach;
    }

    public String getSubAppealCreate() {
        return subAppealCreate;
    }

    public void setSubAppealCreate(String subAppealCreate) {
        this.subAppealCreate = subAppealCreate;
    }

    public String getSubAppealUpdate() {
        return subAppealUpdate;
    }

    public void setSubAppealUpdate(String subAppealUpdate) {
        this.subAppealUpdate = subAppealUpdate;
    }

    public String getTemplateMilestoneApproach() {
        return templateMilestoneApproach;
    }

    public void setTemplateMilestoneApproach(String templateMilestoneApproach) {
        this.templateMilestoneApproach = templateMilestoneApproach;
    }

    public String getTemplateAppealCreate() {
        return templateAppealCreate;
    }

    public void setTemplateAppealCreate(String templateAppealCreate) {
        this.templateAppealCreate = templateAppealCreate;
    }

    public String getTemplateAppealUpdate() {
        return templateAppealUpdate;
    }

    public void setTemplateAppealUpdate(String templateAppealUpdate) {
        this.templateAppealUpdate = templateAppealUpdate;

    }

    public String getSubAppealAccepted() {
        return subAppealAccepted;
    }

    public void setSubAppealAccepted(String subAppealAccepted) {
        this.subAppealAccepted = subAppealAccepted;
    }

    public String getSubAppealRejected() {
        return subAppealRejected;
    }

    public void setSubAppealRejected(String subAppealRejected) {
        this.subAppealRejected = subAppealRejected;
    }

    public String getTemplateAppealAccepted() {
        return templateAppealAccepted;
    }

    public void setTemplateAppealAccepted(String templateAppealAccepted) {
        this.templateAppealAccepted = templateAppealAccepted;
    }

    public String getTemplateAppealRejected() {
        return templateAppealRejected;
    }

    public void setTemplateAppealRejected(String templateAppealRejected) {
        this.templateAppealRejected = templateAppealRejected;
    }

    public String getSubTenderAwarded() {
        return subTenderAwarded;
    }

    public void setSubTenderAwarded(String subTenderAwarded) {
        this.subTenderAwarded = subTenderAwarded;
    }

    public String getTemplateTenderAwarded() {
        return templateTenderAwarded;
    }

    public void setTemplateTenderAwarded(String templateTenderAwarded) {
        this.templateTenderAwarded = templateTenderAwarded;
    }
}
