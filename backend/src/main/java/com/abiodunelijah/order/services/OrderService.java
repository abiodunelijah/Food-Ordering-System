package com.abiodunelijah.order.services;


import com.abiodunelijah.enums.OrderStatus;
import com.abiodunelijah.order.dtos.OrderDto;
import com.abiodunelijah.order.dtos.OrderItemDto;
import com.abiodunelijah.response.Response;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    Response<?> placeOrderFromCart();
    Response<OrderDto> getOrderById(Long id);
    Response<Page<OrderDto>> getAllOrders(OrderStatus orderStatus, int page, int size);
    Response<List<OrderDto>> getOrdersOfUser();
    Response<OrderItemDto> getOrderItemById(Long orderItemId);
    Response<OrderDto> updateOrderStatus(OrderDto orderDTO);
    Response<Long> countUniqueCustomers();
}
