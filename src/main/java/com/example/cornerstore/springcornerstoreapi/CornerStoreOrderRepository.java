package com.example.cornerstore.springcornerstoreapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface CornerStoreOrderRepository extends JpaRepository<CornerStoreOrder, Long> {
//    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%"
//            + " OR p.brand LIKE %?1%"
//            + " OR p.madein LIKE %?1%"
//            + " OR CONCAT(p.price, '') LIKE %?1%")
//    public List<Product> search(String keyword);
}