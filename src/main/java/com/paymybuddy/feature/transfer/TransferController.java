package com.paymybuddy.feature.transfer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class TransferController {

	private final TransferService service;

//	@PostMapping
//	public ResponseEntity<Void> createTransfer(
//			@RequestParam Long senderId,
//			@RequestParam Long receiverId,
//			@RequestParam @Positive @DecimalMax("1000.00") BigDecimal amount,
//			@RequestParam(required = false) String description) { // TODO: Passer par un DTO
//		try {
//			service.createTransfer(senderId, receiverId, amount, description);
//			log.info("Transfer of {} from customer {} to {} created successfully", amount, senderId, receiverId);
//			return ResponseEntity.ok().build();
//		} catch (CustomerNotFoundException e) {
//			log.info("Customer {} or {} not found", senderId, receiverId);
//			return ResponseEntity.notFound().build();
//		} catch (IllegalArgumentException e) {
//			log.info("Invalid transfer request: {}", e.getMessage());
//			return ResponseEntity.badRequest().build();
//		}
//	}

	@GetMapping("/transfer")
	public String showTransfer() {
		return "transfer";
	}

//	@GetMapping
//	public ResponseEntity<List<Transfer>> getTransfers(@RequestParam Long customerId) {
//		try {
//			List<Transfer> transfers = service.getTransfers(customerId);
//			return ResponseEntity.ok(transfers);
//		} catch (CustomerNotFoundException e) {
//			log.info("Customer {} not found", customerId);
//			return ResponseEntity.notFound().build();
//		}
//	}

}
