package com.abiodunelijah.payment.services;


import com.abiodunelijah.email_notification.dtos.NotificationDto;
import com.abiodunelijah.email_notification.services.NotificationService;
import com.abiodunelijah.enums.OrderStatus;
import com.abiodunelijah.enums.PaymentGateway;
import com.abiodunelijah.enums.PaymentStatus;
import com.abiodunelijah.exceptions.BadRequestException;
import com.abiodunelijah.exceptions.NotFoundException;
import com.abiodunelijah.order.entities.Order;
import com.abiodunelijah.order.repository.OrderRepository;
import com.abiodunelijah.payment.dtos.PaymentDto;
import com.abiodunelijah.payment.entities.Payment;
import com.abiodunelijah.payment.repository.PaymentRepository;
import com.abiodunelijah.response.Response;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;
    private final TemplateEngine templateEngine;
    private final ModelMapper modelMapper;


    @Value("${stripe.api.secret.key}")
    private String secreteKey;

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;


    @Override
    public Response<?> initializePayment(PaymentDto paymentRequest) {

        log.info("Inside initializePayment()");
        Stripe.apiKey = secreteKey;

        Long orderId = paymentRequest.getOrderId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order Not Found"));


        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new BadRequestException("Payment Already Made For This Order");
        }

        log.info("Payment Not Made For This order Yet ...moving");
        log.info("Payment Request Amount IS: {}", paymentRequest.getAmount());

        if (order.getTotalAmount() == null || paymentRequest.getAmount() == null) {
            log.info("Amount is likely null");
            throw new BadRequestException("Amount you are passing in is null");
        }

        log.info("Amount is not null ... moving forward ...");

        if (order.getTotalAmount().compareTo(paymentRequest.getAmount()) != 0) {
            log.info("Payment Amount Does Not Tally. Please Contact Out Customer Support Agent");
            throw new BadRequestException("Payment Amount Does Not Tally. Please Contact Out Customer Support Agent");
        }

        log.info("Payment amount tally...moving");

        //create payment intent i.e create unique transaction id for that payment
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).longValue()) // converting to cent
                    .setCurrency("usd")
                    .putMetadata("orderId", String.valueOf(orderId))
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            String uniqueTransactionId = intent.getClientSecret();

            return Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("success")
                    .data(uniqueTransactionId)
                    .build();

        } catch (Exception e) {
            log.info("ERROR in PaymentIntentCreateParams" + e.getMessage());
            throw new RuntimeException("Error Creating payment unique transaction id");
        }
    }



    @Override
    public void updatePaymentForOrder(PaymentDto paymentDTO) {

        log.info("inside updatePaymentForOrder()");

        Long orderId = paymentDTO.getOrderId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order Not Found"));

        //  Build payment entity to save
        Payment payment = new Payment();
        payment.setPaymentGateway(PaymentGateway.STRIPE);
        payment.setAmount(paymentDTO.getAmount());
        payment.setTransactionId(paymentDTO.getTransactionId());
        payment.setPaymentStatus(paymentDTO.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setOrder(order);
        payment.setUser(order.getUser());

        if (!paymentDTO.isSuccess()) {
            payment.setFailureReason(paymentDTO.getFailureReason());
        }

        paymentRepository.save(payment);

        // Prepare email context. Context should be. imported from thymeleaf
        Context context = new Context(Locale.getDefault());
        context.setVariable("customerName", order.getUser().getName());
        context.setVariable("orderId", order.getId());
        context.setVariable("currentYear", Year.now().getValue());
        context.setVariable("amount", "$" + paymentDTO.getAmount());

        if (paymentDTO.isSuccess()) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            order.setOrderStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);


            log.info("PAYMENT IS SUCCESSFUL ABOUT TO SEND EMAIL");

            // Add success-specific variables
            context.setVariable("transactionId", paymentDTO.getTransactionId());
            context.setVariable("paymentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")));
            context.setVariable("frontendBaseUrl", this.frontendBaseUrl);

            String emailBody = templateEngine.process("payment-success", context);

            log.info("HAVE GOTTEN TEMPLATE");

            notificationService.sendEmail(NotificationDto.builder()
                    .recipient(order.getUser().getEmail())
                    .subject("Payment Successful - Order #" + order.getId())
                    .body(emailBody)
                    .isHtml(true)
                    .build());
        } else {
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);


            log.info("PAYMENT IS FAILED ABOUT TO SEND EMAIL");
            // Add failure-specific variables
            context.setVariable("failureReason", paymentDTO.getFailureReason());

            String emailBody = templateEngine.process("payment-failed", context);

            notificationService.sendEmail(NotificationDto.builder()
                    .recipient(order.getUser().getEmail())
                    .subject("Payment Failed - Order #" + order.getId())
                    .body(emailBody)
                    .isHtml(true)
                    .build());
        }
    }


    @Override
    public Response<List<PaymentDto>> getAllPayments() {

        log.info("inside getAllPayments()");

        List<Payment> paymentList = paymentRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<PaymentDto> paymentDTOS = modelMapper.map(paymentList, new TypeToken<List<PaymentDto>>() {}.getType());

        paymentDTOS.forEach(item -> {
            item.setOrderDto(null);
            item.setUserDto(null);
        });

        return Response.<List<PaymentDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("payment retreived succeessfully")
                .data(paymentDTOS)
                .build();

    }


    @Override
    public Response<PaymentDto> getPaymentById(Long paymentId) {

        log.info("inside getPaymentById()");

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(()-> new NotFoundException("Payment not found"));
        PaymentDto paymentDTOS = modelMapper.map(payment, PaymentDto.class);

        paymentDTOS.getUserDto().setRoles(null);
        paymentDTOS.getOrderDto().setUser(null);
        paymentDTOS.getOrderDto().getOrderItems().forEach(item->{
            item.getMenu().setReviews(null);
        });

        return Response.<PaymentDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("payment retreived succeessfully by id")
                .data(paymentDTOS)
                .build();
    }
}
