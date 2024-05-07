package com.tech.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tech.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	@Query("select p from Payment p where p.orderNumber = :orderNumber")
	Payment findByOrderNumbe(@Param("orderNumber") String orderNumber) ;

}
