package com.abiodunelijah.cart.dtos;

import com.abiodunelijah.menu.dtos.MenuDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartItemDto {

    private Long id;
    private MenuDto menuDto;
    private int quantity;

    private BigDecimal pricePerUnit;

    private BigDecimal subTotal;
}
