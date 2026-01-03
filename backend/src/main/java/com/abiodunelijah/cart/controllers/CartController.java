package com.abiodunelijah.cart.controllers;


import com.abiodunelijah.cart.dtos.CartDto;
import com.abiodunelijah.cart.services.CartService;
import com.abiodunelijah.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<Response<?>> addItemToCart(@RequestBody CartDto cartDTO){
        return ResponseEntity.ok(cartService.addItemToCart(cartDTO));
    }

    @PutMapping("/items/increment/{menuId}")
    public ResponseEntity<Response<?>> incrementItem(@PathVariable Long menuId){
        return ResponseEntity.ok(cartService.incrementMenuItem(menuId));
    }

    @PutMapping("/items/decrement/{menuId}")
    public ResponseEntity<Response<?>> decrementItem(@PathVariable Long menuId){
        return ResponseEntity.ok(cartService.decrementMenuItem(menuId));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Response<?>> removeItem(@PathVariable Long cartItemId){
        return ResponseEntity.ok(cartService.removeMenuItem(cartItemId));
    }


    @GetMapping
    public ResponseEntity<Response<CartDto>> getShoppingCart(){
        return ResponseEntity.ok(cartService.getShoppingCart());
    }

    @DeleteMapping
    public ResponseEntity<Response<?>> clearShoppingCart(){
        return ResponseEntity.ok(cartService.clearShoppingCart());
    }



}
