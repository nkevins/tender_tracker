package com.chlorocode.tendertracker.service.notification;

import java.util.Map;

/**
 * Kyaw Min Thu
 */
public interface NotificationService {
    void sendNotification(NotificationServiceImpl.NOTI_MODE mode, Map<String, Object> params);
}
