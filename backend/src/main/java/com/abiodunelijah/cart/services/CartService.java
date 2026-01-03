package com.abiodunelijah.cart.services;


import com.abiodunelijah.cart.dtos.CartDto;
import com.abiodunelijah.response.Response;

public interface CartService {

    Response<?> addItemToCart(CartDto cartDto);
    Response<?> incrementMenuItem(Long menuItemId);
    Response<?> decrementMenuItem(Long menuItemId);
    Response<?> removeMenuItem(Long cartItem);
    Response<CartDto> getShoppingCart();
    Response<?> clearShoppingCart();

}
