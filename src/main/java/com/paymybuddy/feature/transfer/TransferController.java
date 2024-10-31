package com.paymybuddy.feature.transfer;

import com.paymybuddy.feature.customer.CustomerDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class TransferController {

	private final TransferService service;

	@GetMapping("/transfer")
	public String showTransferPage(Model model, HttpSession session) {
		session.setAttribute("customer", CustomerDTO.builder().email("bernard@mail.com").username("nanard").build()); // TODO: supprimer ce vilain traficotage de session

		log.info("Entering /transfer endpoint");
		log.info("Session ID: {}", session.getId());
		log.info("Session attributes: {}", Collections.list(session.getAttributeNames()));

		if (!isCustomerLoggedIn(session)) {
			log.warn("Customer is not logged in, redirect to login page");
			return "redirect:/login";
		}
		CustomerDTO customer = (CustomerDTO) session.getAttribute("customer");
		List<Transfer> transfers = service.getTransfersForCustomer(customer.getUsername());

		model.addAttribute("transfers", transfers);
		return "transfer";
	}

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

	private boolean isCustomerLoggedIn(HttpSession session) {
		return session.getAttribute("customer") != null;
	}

}
