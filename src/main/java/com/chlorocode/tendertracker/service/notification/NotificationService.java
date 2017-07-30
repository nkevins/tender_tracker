package com.chlorocode.tendertracker.service.notification;

import com.chlorocode.tendertracker.dao.entity.User;

import java.util.List;

/**
 * Kyaw Min Thu
 */
public interface NotificationService {
    public boolean sendNotification(NotificationServiceImpl.NOTI_MODE mode, User... to);
}
