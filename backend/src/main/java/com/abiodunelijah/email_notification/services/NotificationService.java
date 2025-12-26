package com.abiodunelijah.email_notification.services;

import com.abiodunelijah.email_notification.dtos.NotificationDto;

public interface NotificationService {
    void sendEmail(NotificationDto notificationDto);
}
