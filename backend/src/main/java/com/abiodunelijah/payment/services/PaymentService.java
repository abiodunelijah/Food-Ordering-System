package com.abiodunelijah.payment.services;


import com.abiodunelijah.payment.dtos.PaymentDto;
import com.abiodunelijah.response.Response;

import java.util.List;

public interface PaymentService {

    Response<?> initializePayment(PaymentDto paymentDTO);
    void updatePaymentForOrder(PaymentDto paymentDTO);
    Response<List<PaymentDto>> getAllPayments();
    Response<PaymentDto> getPaymentById(Long paymentId);

}
