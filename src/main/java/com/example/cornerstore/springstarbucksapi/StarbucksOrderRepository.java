package com.example.cornerstore.springstarbucksapi;

import org.springframework.data.jpa.repository.JpaRepository;

interface StarbucksOrderRepository extends JpaRepository<StarbucksOrder, Long> {
}