package com.chlorocode.tendertracker.dao.dto;

import org.springframework.validation.ObjectError;

import java.util.LinkedList;
import java.util.List;

public class AlertDTO {

    public enum AlertType {
        INFO, SUCCESS, DANGER, WARNING
    }

    private AlertType alertType;
    private List<String> messages;

    public AlertDTO(AlertType alertType, String message) {
        this.alertType = alertType;
        messages = new LinkedList<>();
        messages.add(message);
    }

    public AlertDTO(List<ObjectError> errors) {
        this.alertType = AlertType.DANGER;
        messages = new LinkedList<>();
        for (ObjectError e : errors) {
            messages.add(e.getDefaultMessage());
        }
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public String getType() {
        return alertType.toString().toLowerCase();
    }
}
