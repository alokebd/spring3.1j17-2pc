package com.tech.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tech.payment.dto.PaymentStatus;
import com.tech.payment.dto.TransactionData;
import com.tech.payment.entity.Payment;
import com.tech.payment.repository.PaymentRepository;
import com.tech.payment.service.PaymentService;

@RestController
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@PostMapping("/prepare_payment")
	public ResponseEntity<String> preparePayment(@RequestBody TransactionData transactionData) {
		try {
			Payment payment = new Payment();
			payment.setOrderNumber(transactionData.getOrderNumber());
			payment.setItem(transactionData.getItem());
			String paymentStatus = transactionData.getPaymentMode();
			if (!paymentStatus.isEmpty() && paymentStatus.equalsIgnoreCase(PaymentStatus.PENDING.name())) {
				payment.setPreparationStatus(PaymentStatus.PENDING.name());
			}
			else {
				payment.setPreparationStatus(PaymentStatus.CANCELLED.name());
			}
			payment.setPrice(transactionData.getPrice());
			payment.setPaymentMode(transactionData.getPaymentMode());
			
			Payment dbPayment= paymentService.savePayment(payment);
			
			if (dbPayment == null || !dbPayment.getOrderNumber().equalsIgnoreCase(transactionData.getOrderNumber())) {
				throw new RuntimeException("Prepare phase failed for payment " + transactionData.getOrderNumber());
			}

			return ResponseEntity.ok("Payment prepared successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during payment preparation");
		}
	}


	@PostMapping("/commit_payment")
	public ResponseEntity<String> commitOrder(@RequestBody TransactionData transactionData) {
		Payment payment = paymentService.findByOrderNumbe(transactionData.getOrderNumber());

		if (payment != null && payment.getPreparationStatus().equalsIgnoreCase(PaymentStatus.PENDING.name())) {
			payment.setPreparationStatus(PaymentStatus.APPROVED.name());
			paymentService.savePayment(payment);


			return ResponseEntity.ok("Payment committed successfully");
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment cannot be committed");
	}

	@PostMapping("/rollback_payment")
	public ResponseEntity<String> rollbackOrder(@RequestBody TransactionData transactionData) {
		Payment payment = paymentService.findByOrderNumbe(transactionData.getOrderNumber());

		if (payment != null) {
			payment.setPreparationStatus(PaymentStatus.ROLLBACK.name());
			paymentService.savePayment(payment);

			return ResponseEntity.ok("Payment rolled back successfully");
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during Payment rollback");
	}

}
