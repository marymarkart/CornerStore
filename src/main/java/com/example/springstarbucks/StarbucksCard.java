package com.example.springstarbucks;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Table(indexes = @Index(name = "altIndex", columnList = "cardNumber", unique = true))
@Data
@RequiredArgsConstructor
public class StarbucksCard {

    @Id @GeneratedValue private Long id;
    @Column(nullable = false) private String cardNumber;
    @Column(nullable = false) private String cardCode;
    @Column(nullable = false) private double balance;
    @Column(nullable = false) private boolean activated;
    private String status;
}
