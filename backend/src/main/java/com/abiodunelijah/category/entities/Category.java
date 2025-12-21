package com.abiodunelijah.category.entities;

import com.abiodunelijah.menu.entities.Menu;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;


    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Menu> menus;

}
