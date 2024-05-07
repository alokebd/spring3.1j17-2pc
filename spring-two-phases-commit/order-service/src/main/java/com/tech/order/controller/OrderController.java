package com.tech.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tech.order.dto.OrderPreparationStatus;
import com.tech.order.dto.TransactionData;
import com.tech.order.entity.Order;
import com.tech.order.repository.OrderRepository;
import com.tech.order.service.OrderService;

@RestController
public class OrderController {
	
	@Autowired
	private OrderService orderService;

	@PostMapping("/prepare_order")
	public ResponseEntity<String> prepareOrder(@RequestBody TransactionData transactionData) {
		try {
			Order order = new Order();
			order.setOrderNumber(transactionData.getOrderNumber());
			order.setItem(transactionData.getItem());
			String strStatus =transactionData.getPaymentMode();
			if (!strStatus.isEmpty() && !strStatus.equalsIgnoreCase((OrderPreparationStatus.NOT_PREPARED.name()))) {
				order.setPreparationStatus(OrderPreparationStatus.PREPARING.name());
			}
			else {
				order.setPreparationStatus(OrderPreparationStatus.NOT_PREPARED.name());
			}
			
			Order dbOrder =orderService.savedOrder(order); 
			if (dbOrder==null || !transactionData.getOrderNumber().equals(dbOrder.getOrderNumber().trim())) {
				throw new RuntimeException("Prepare phase failed for order " + transactionData.getOrderNumber());
			}
			
			return ResponseEntity.ok("Order prepared successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during oredr preparation");
		}
	}


	@PostMapping("/commit_order")
	public ResponseEntity<String> commitOrder(@RequestBody TransactionData transactionData) {
		Order order = orderService.findByOrderNumber(transactionData.getOrderNumber());
		if (order != null && 
				order.getPreparationStatus().trim().equalsIgnoreCase(OrderPreparationStatus.PREPARING.name())) {
			order.setPreparationStatus(OrderPreparationStatus.COMMITTED.name());
			orderService.savedOrder(order);

			return ResponseEntity.ok("Order committed successfully");
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Order cannot be committed");
	}

	@PostMapping("/rollback_order")
	public ResponseEntity<String> rollbackOrder(@RequestBody TransactionData transactionData) {
		Order order = orderService.findByOrderNumber(transactionData.getOrderNumber());
		if (order != null) {
			order.setPreparationStatus(OrderPreparationStatus.ROLLBACK.name());
			orderService.savedOrder(order);

			return ResponseEntity.ok("Order rolled back successfully");
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during order rollback");
	}

}
