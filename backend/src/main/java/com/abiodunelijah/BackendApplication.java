package com.abiodunelijah;

import com.abiodunelijah.email_notification.dtos.NotificationDto;
import com.abiodunelijah.email_notification.services.NotificationService;
import com.abiodunelijah.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class BackendApplication {

    private final NotificationService notificationService;

    static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(){
        return args -> {
            NotificationDto notificationDto = NotificationDto.builder()
                    .recipient("abiodunelijah.yb@gmail.com")
                    .subject("Email Subject Test")
                    .body("Hello World, This is a test")
                    .type(NotificationType.EMAIL)
                    .build();

            notificationService.sendEmail(notificationDto);
        };
    }

}
