package com.example.springpayments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface PaymentsCommandRepository extends JpaRepository<PaymentsCommand, Long> {
}
