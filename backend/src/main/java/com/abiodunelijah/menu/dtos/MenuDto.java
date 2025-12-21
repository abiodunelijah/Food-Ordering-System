package com.abiodunelijah.menu.dtos;

import com.abiodunelijah.category.entities.Category;
import com.abiodunelijah.order.entities.OrderItem;
import com.abiodunelijah.review.entities.Review;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuDto {

    private Long id;

    @NotBlank(message = "Name is required.")
    private String name;

    private String description;

    @NotNull(message = "Price is required.")
    @Positive(message = "Price must be positive.")
    private BigDecimal price;

    private String imageUrl;

    private Category category;

    private List<OrderItem> orderItems;

    private List<Review> reviews;

}
