package com.example.cornerstore.springstarbucksapi;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Table(indexes = @Index(name = "altIndex", columnList = "id", unique = true))
@Data
@RequiredArgsConstructor
public class StarbucksOrder {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false) private String drink;
    @Column(nullable = false) private String milk;
    @Column(nullable = false) private String size;
    @Column(nullable = false) private double total;
    private String status;
}
