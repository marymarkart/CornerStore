package com.example.cornerstore.springcornerstoreapi;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@RequiredArgsConstructor
public class Product {
    @Id
    @GeneratedValue
    Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String listing;
    @Column(nullable = false) private String description;
    @Column(nullable = false) private double price;
    @Column(nullable = false) private boolean sold;
}
