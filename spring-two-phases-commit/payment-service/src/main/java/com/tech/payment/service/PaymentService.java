package com.tech.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tech.payment.entity.Payment;
import com.tech.payment.repository.PaymentRepository;

@Service
public class PaymentService {
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	public Payment savePayment (Payment payment) {
		return paymentRepository.save(payment);
	}
	
	public Payment findByOrderNumbe(String orderNumbe) {
		return paymentRepository.findByOrderNumbe(orderNumbe);
	}

}
