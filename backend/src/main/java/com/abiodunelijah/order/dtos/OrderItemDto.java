package com.abiodunelijah.order.dtos;

import com.abiodunelijah.menu.dtos.MenuDto;
import com.abiodunelijah.menu.entities.Menu;
import com.abiodunelijah.order.entities.Order;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDto {

    private Long id;

    private long menuId;

    private MenuDto menuDto;

    private int quantity;

    private BigDecimal pricePerUnit;

    private BigDecimal subtotal;
}
