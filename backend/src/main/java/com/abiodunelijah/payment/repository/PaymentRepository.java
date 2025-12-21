package com.abiodunelijah.payment.repository;

import com.abiodunelijah.payment.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
