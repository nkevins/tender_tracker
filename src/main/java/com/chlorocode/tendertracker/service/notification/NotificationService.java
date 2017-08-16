package com.chlorocode.tendertracker.service.notification;

import com.chlorocode.tendertracker.dao.entity.User;

import java.util.List;
import java.util.Map;

/**
 * Kyaw Min Thu
 */
public interface NotificationService {
    void sendNotification(NotificationServiceImpl.NOTI_MODE mode, Map<String, Object> params);
}
