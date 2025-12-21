package com.abiodunelijah.email_notification.dtos;

import com.abiodunelijah.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class NotificationDto {

    private Long id;
    private String subject;
    private String recipient;
    private String body;
    private NotificationType type;
    private final LocalDateTime createdAt;
    private boolean isHtml;

}
