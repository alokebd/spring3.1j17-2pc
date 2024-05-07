package com.tech.coordinator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.tech.coordinator.dto.TransactionData;
import com.tech.coordinator.service.CoordinatorService;

@RestController
public class CoordinatorController {
    @Autowired
	private CoordinatorService coordinatorService;

	@PostMapping("/initiate_2pc")
	public String intiateTwoPhaseCommit(@RequestBody TransactionData transactionData) {
		if (coordinatorService.callPreparePhase(transactionData)) {
			if (coordinatorService.callCommitPhase(transactionData)) {
				return "Transavction committed successfully.";
			}
			coordinatorService.callRollback(transactionData);
			return "Transaction Rollback";
		}
		coordinatorService.callRollback(transactionData);
		return "Transaction Rollback";
	}
}
