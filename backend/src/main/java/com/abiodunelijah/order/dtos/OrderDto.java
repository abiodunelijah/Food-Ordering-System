package com.abiodunelijah.order.dtos;

import com.abiodunelijah.auth_users.dtos.UserDto;
import com.abiodunelijah.enums.OrderStatus;
import com.abiodunelijah.enums.PaymentStatus;
import com.abiodunelijah.payment.dtos.PaymentDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {

    private Long id;

    private UserDto user;

    private LocalDateTime orderDate;

    private BigDecimal totalAmount;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;

    private PaymentDto payment;

    private List<OrderItemDto> orderItems;

}
