package com.abiodunelijah.email_notification.repository;

import com.abiodunelijah.email_notification.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
