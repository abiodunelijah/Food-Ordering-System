package com.abiodunelijah.payment.entities;

import com.abiodunelijah.auth_users.entities.User;
import com.abiodunelijah.enums.PaymentGateway;
import com.abiodunelijah.enums.PaymentStatus;
import com.abiodunelijah.order.entities.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "payments")
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String transactionId;

    @Enumerated(EnumType.STRING)
    private PaymentGateway  paymentGateway;

    private String failureReason;

    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
