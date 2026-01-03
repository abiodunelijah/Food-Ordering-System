package com.abiodunelijah.cart.entities;


import com.abiodunelijah.auth_users.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "carts")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    private String promoteCode;
}
