package com.abiodunelijah.order.repository;

import com.abiodunelijah.order.dtos.OrderItemDto;
import com.abiodunelijah.order.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    @Query("SELECT CASE WHEN COUNT (oi) > 0 THEN true ELSE false END  " +
            "FROM OrderItem oi " +
            "WHERE oi.order.id = : orderId AND oi.menu.id = :menuID")
    boolean existsByOrderIdAndMenuId(@Param("orderId") Long orderId, @Param("menuId") Long menuId);
}
