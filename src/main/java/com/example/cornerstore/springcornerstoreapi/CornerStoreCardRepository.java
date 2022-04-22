package com.example.cornerstore.springcornerstoreapi;

import org.springframework.data.jpa.repository.JpaRepository;

interface CornerStoreCardRepository extends JpaRepository<CornerStoreCard, Long> {
    CornerStoreCard findByCardNumber(String cardNumber);
}