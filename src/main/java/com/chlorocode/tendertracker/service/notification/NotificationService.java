package com.chlorocode.tendertracker.service.notification;

import java.util.Map;

/**
 * Service interface for notification service.
 */
public interface NotificationService {

    /**
     * This method is used to send notification to user.
     *
     * @param mode notification mode
     * @param params parameters
     */
    void sendNotification(NotificationServiceImpl.NOTI_MODE mode, Map<String, Object> params);
}
