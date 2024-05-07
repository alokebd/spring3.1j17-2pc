package com.tech.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tech.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("select o from Order o where o.orderNumber = :orderNumber")
	Order findByOrderNumber(@Param("orderNumber") String orderNumber);

}
