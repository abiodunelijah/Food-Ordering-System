package com.abiodunelijah.order.repository;

import com.abiodunelijah.auth_users.entities.User;
import com.abiodunelijah.enums.OrderStatus;
import com.abiodunelijah.order.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {

    Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    List<Order> findByUserOrderByOrderDateDesc(User user);

    @Query("SELECT COUNT(DISTINCT  o.user.id) FROM Order o")
    long countDistinctUsers();

}
