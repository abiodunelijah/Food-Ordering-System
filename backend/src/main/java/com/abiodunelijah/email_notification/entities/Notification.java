package com.abiodunelijah.email_notification.entities;

import com.abiodunelijah.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    @NotBlank(message = "Recipient is required.")
    private String recipient;

    @Lob
    private String body;

    private NotificationType type;

    private final LocalDateTime createdAt = LocalDateTime.now();

    private boolean isHtml;


}
