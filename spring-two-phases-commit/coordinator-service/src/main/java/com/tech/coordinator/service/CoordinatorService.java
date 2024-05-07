package com.tech.coordinator.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tech.coordinator.dto.TransactionData;

@Service
public class CoordinatorService {
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	
	public boolean callPreparePhase(TransactionData transactionData) {
		try {
			boolean isOrderSuccess = callServices("http://localhost:8081/prepare_order", transactionData);
			boolean isPaymentSuccess = callServices("http://localhost:8082/prepare_payment", transactionData);
			return isOrderSuccess && isPaymentSuccess;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean callCommitPhase(TransactionData transactionData) {
		boolean isOrderSuccess = callServices("http://localhost:8081/commit_order", transactionData);
		boolean isPaymentSuccess = callServices("http://localhost:8082/commit_payment", transactionData);
		return isOrderSuccess && isPaymentSuccess;
	}

	private boolean callServices(String url, TransactionData transactionData) {
		ResponseEntity<String> response = restTemplate.postForEntity(url, transactionData, String.class);
		return response.getStatusCode().is2xxSuccessful();
	}

	public void callRollback(TransactionData transactionData) {
		callServiceRollbackPhase("http://localhost:8081/rollback_order", transactionData);
		callServiceRollbackPhase("http://localhost:8082/rollback_payment", transactionData);

	}

	private void callServiceRollbackPhase(String serviceUrl, TransactionData transactionData) {
		restTemplate.postForEntity(serviceUrl, transactionData, Void.class);
	}

}
