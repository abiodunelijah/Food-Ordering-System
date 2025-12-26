package com.abiodunelijah.email_notification.services;

import com.abiodunelijah.email_notification.dtos.NotificationDto;
import com.abiodunelijah.email_notification.entities.Notification;
import com.abiodunelijah.email_notification.repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;

    @Override
    @Async
    public void sendEmail(NotificationDto notificationDto) {
        log.info("Inside sendEmail");

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            messageHelper.setTo(notificationDto.getRecipient());
            messageHelper.setSubject(notificationDto.getSubject());
            messageHelper.setText(notificationDto.getBody(), notificationDto.isHtml());

            javaMailSender.send(mimeMessage);

            //Save To Database
            Notification notificationToSave = Notification.builder()
                    .recipient(notificationDto.getRecipient())
                    .subject(notificationDto.getSubject())
                    .body(notificationDto.getBody())
                    .type(notificationDto.getType())
                    .isHtml(notificationDto.isHtml())
                    .build();

            notificationRepository.save(notificationToSave);

            log.info("Notification has been saved successfully");


        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
