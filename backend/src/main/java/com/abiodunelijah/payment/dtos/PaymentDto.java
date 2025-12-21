package com.abiodunelijah.payment.dtos;

import com.abiodunelijah.auth_users.dtos.UserDto;
import com.abiodunelijah.auth_users.entities.User;
import com.abiodunelijah.enums.PaymentGateway;
import com.abiodunelijah.enums.PaymentStatus;
import com.abiodunelijah.order.dtos.OrderDto;
import com.abiodunelijah.order.entities.Order;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDto {

    private Long id;


    private long orderId;

    private BigDecimal amount;


    private PaymentStatus paymentStatus;

    private String transactionId;


    private PaymentGateway paymentGateway;

    private String failureReason;

    private boolean success;

    private LocalDateTime paymentDate;

    private OrderDto orderDto;
    private UserDto userDto;

}
