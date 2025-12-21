package com.abiodunelijah.cart.repository;

import com.abiodunelijah.cart.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
