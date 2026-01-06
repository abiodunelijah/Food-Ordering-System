package com.abiodunelijah.order.services;


import com.abiodunelijah.auth_users.entities.User;
import com.abiodunelijah.auth_users.services.UserService;
import com.abiodunelijah.cart.entities.Cart;
import com.abiodunelijah.cart.entities.CartItem;
import com.abiodunelijah.cart.repository.CartRepository;
import com.abiodunelijah.cart.services.CartService;
import com.abiodunelijah.email_notification.dtos.NotificationDto;
import com.abiodunelijah.email_notification.services.NotificationService;
import com.abiodunelijah.enums.OrderStatus;
import com.abiodunelijah.enums.PaymentStatus;
import com.abiodunelijah.exceptions.BadRequestException;
import com.abiodunelijah.exceptions.NotFoundException;
import com.abiodunelijah.menu.dtos.MenuDto;
import com.abiodunelijah.order.dtos.OrderDto;
import com.abiodunelijah.order.dtos.OrderItemDto;
import com.abiodunelijah.order.entities.Order;
import com.abiodunelijah.order.entities.OrderItem;
import com.abiodunelijah.order.repository.OrderItemRepository;
import com.abiodunelijah.order.repository.OrderRepository;
import com.abiodunelijah.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final CartService cartService;
    private final CartRepository cartRepository;

    @Value("${base.payment.link}")
    private String basedPaymentLink;



    @Transactional
    @Override
    public Response<?> placeOrderFromCart() {

        log.info("Inside placeOrderFromCart()");

        User customer = userService.getCurrentLoggedInUser();

        log.info("user passed");

        String deliveryAddress = customer.getAddress();

        log.info("deliveryAddress passed");

        if (deliveryAddress == null) {
            throw new NotFoundException("Delivery Address Not present for the user");
        }
        Cart cart = cartRepository.findByUser_Id(customer.getId())
                .orElseThrow(()-> new NotFoundException("Cart not found for the user" ));


        log.info("cart passed");

        List<CartItem> cartItems = cart.getCartItems();

        log.info("cartItems passed");

        if (cartItems == null || cartItems.isEmpty()) throw new BadRequestException("Cart is empty");

        List<OrderItem> orderItems = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;


        log.info("totalAmount passed");

        for (CartItem cartItem: cartItems){

            OrderItem orderItem = OrderItem.builder()
                    .menu(cartItem.getMenu())
                    .quantity(cartItem.getQuantity())
                    .pricePerUnit(cartItem.getPricePerUnit())
                    .subtotal(cartItem.getSubTotal())
                    .build();
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        log.info("orderItem adding passed");

        Order order = Order.builder()
                .user(customer)
                .orderItems(orderItems)
                .orderDate(LocalDateTime.now())
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.INITIALIZED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();


        log.info("order build passed");

        Order savedOrder = orderRepository.save(order); //save order


        log.info("order saved passed");

        orderItems.forEach(orderItem -> orderItem.setOrder(savedOrder));

        orderItemRepository.saveAll(orderItems); //save order item


        log.info("order items saved");

        // Clear the user's cart after the order is placed
        cartService.clearShoppingCart();

        log.info("shopping cart cleared");

        OrderDto orderDTO = modelMapper.map(savedOrder, OrderDto.class);


        log.info("model mappern mapped savedOrder to OrderDto");

        // Send email notifications
        sendOrderConfirmationEmail(customer, orderDTO);


        log.info("building response to send");


        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your order has been received! We've sent a secure payment link to your email. Please proceed for payment to confirm your order.")
                .build();

    }

    @Override
    public Response<OrderDto> getOrderById(Long id) {

        log.info("Inside getOrderById()");
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Order Not Found"));

        OrderDto orderDTO = modelMapper.map(order, OrderDto.class);

        return Response.<OrderDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Order retrieved successfully")
                .data(orderDTO)
                .build();
    }

    @Override
    public Response<Page<OrderDto>> getAllOrders(OrderStatus orderStatus, int page, int size) {
        log.info("Inside getAllOrders()");

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Order> orderPage;

        if (orderStatus != null){
            orderPage = orderRepository.findByOrderStatus(orderStatus, pageable);
        }else {
            orderPage = orderRepository.findAll(pageable);
        }

        Page<OrderDto> orderDTOPage  = orderPage.map(order -> {
            OrderDto dto = modelMapper.map(order, OrderDto.class);
            dto.getOrderItems().forEach(orderItemDTO -> orderItemDTO.getMenu().setReviews(null));
            return dto;
        });


        return Response.<Page<OrderDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Orders retrieved successfully")
                .data(orderDTOPage)
                .build();

    }

    @Override
    public Response<List<OrderDto>> getOrdersOfUser() {
        log.info("Inside getOrdersOfUser()");

        User customer = userService.getCurrentLoggedInUser();
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(customer);

        List<OrderDto> orderDTOS = orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .toList();

        orderDTOS.forEach(orderItem -> {
            orderItem.setUser(null);
            orderItem.getOrderItems().forEach(item-> item.getMenu().setReviews(null));
        });


        return Response.<List<OrderDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Orders for user retrieved successfully")
                .data(orderDTOS)
                .build();

    }

    @Override
    public Response<OrderItemDto> getOrderItemById(Long orderItemId) {

        log.info("Inside getOrderItemById()");

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(()-> new NotFoundException("Order Item Not Found"));


        OrderItemDto orderItemDTO = modelMapper.map(orderItem, OrderItemDto.class);

        orderItemDTO.setMenu(modelMapper.map(orderItem.getMenu(), MenuDto.class));


        return Response.<OrderItemDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("OrderItem retrieved successfully")
                .data(orderItemDTO)
                .build();

    }

    @Override
    public Response<OrderDto> updateOrderStatus(OrderDto orderDTO) {
        log.info("Inside updateOrderStatus()");


        Order order = orderRepository.findById(orderDTO.getId())
                .orElseThrow(() -> new NotFoundException("Order not found: "));

        OrderStatus orderStatus = orderDTO.getOrderStatus();
        order.setOrderStatus(orderStatus);

        orderRepository.save(order);

        return Response.<OrderDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Order status updated successfully")
                .build();
    }

    @Override
    public Response<Long> countUniqueCustomers() {
        log.info("Inside countUniqueCustomers()");

        long uniqueCustomerCount = orderRepository.countDistinctUsers();
        return Response.<Long>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Unique customer count retrieved successfully")
                .data(uniqueCustomerCount)
                .build();
    }



    private void sendOrderConfirmationEmail(User customer, OrderDto orderDTO){

        String subject =  "Your Order Confirmation - Order #" + orderDTO.getId();

        //create a Thymeleaf contex and set variables. import the context from Thymeleaf
        Context context = new Context(Locale.getDefault());

        context.setVariable("customerName", customer.getName());
        context.setVariable("orderId", String.valueOf(orderDTO.getId()));
        context.setVariable("orderDate", orderDTO.getOrderDate().toString());
        context.setVariable("totalAmount", orderDTO.getTotalAmount().toString());

        // Format delivery address
        String deliveryAddress = orderDTO.getUser().getAddress();
        context.setVariable("deliveryAddress", deliveryAddress);

        context.setVariable("currentYear", java.time.Year.now());

        // Build the order items HTML using StringBuilder
        StringBuilder orderItemsHtml = new StringBuilder();

        for (OrderItemDto item : orderDTO.getOrderItems()) {
            orderItemsHtml.append("<div class=\"order-item\">")
                    .append("<p>").append(item.getMenu().getName()).append(" x ").append(item.getQuantity()).append("</p>")
                    .append("<p> $ ").append(item.getSubtotal()).append("</p>")
                    .append("</div>");
        }

        context.setVariable("orderItemsHtml", orderItemsHtml.toString());
        context.setVariable("totalItems", orderDTO.getOrderItems().size());


        String paymentLink = basedPaymentLink + orderDTO.getId() + "&amount=" + orderDTO.getTotalAmount(); // Replace "yourdomain.com"
        context.setVariable("paymentLink", paymentLink);

        // Process the Thymeleaf template to generate the HTML email body
        String emailBody = templateEngine.process("order-confirmation", context);  // "order-confirmation" is the template name

        notificationService.sendEmail(NotificationDto.builder()
                .recipient(customer.getEmail())
                .subject(subject)
                .body(emailBody)
                .isHtml(true)
                .build());

    }

}
