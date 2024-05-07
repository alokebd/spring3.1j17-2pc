package com.tech.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tech.order.entity.Order;
import com.tech.order.repository.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	public Order savedOrder(Order order) {
		return orderRepository.save(order);
	}
	
	public Order findByOrderNumber(String orderId) {
		return orderRepository.findByOrderNumber(orderId);
	}

}
