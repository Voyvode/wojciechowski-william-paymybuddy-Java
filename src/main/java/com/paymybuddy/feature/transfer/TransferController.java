package com.paymybuddy.feature.transfer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class TransferController {

	private final TransferService service;

	@GetMapping("/transfer")
	public String displayTransferPage(Model model, HttpSession session, HttpServletRequest request) {
		if (!isCustomerLoggedIn(request)) {
			log.warn("Customer is not logged in, redirect to login page");
			return "redirect:/login";
		}

		var username = session.getAttribute("username").toString();
		List<Transfer> transfers = service.getTransfersForCustomer(username);

		model.addAttribute("transfers", transfers);
		log.info("Displaying transfer page");
		return "transfer";
	}

	/**
	 * TODO: Pas encore opérationnel
	 *
	 * @param newTransfer
	 * @param result
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/transfer/x")
	public String createTransfer(@ModelAttribute @Validated TransferDTO newTransfer, BindingResult result, RedirectAttributes redirectAttributes) {
		try {
			service.createTransfer(newTransfer.getSenderUsername(), newTransfer.getReceiverUsername(), newTransfer.getAmount(), newTransfer.getDescription());
			log.info("{} sent {}€ to {}", newTransfer.getSenderUsername(), newTransfer.getAmount(), newTransfer.getReceiverUsername());
			return "redirect:/transfer";
		} catch (NoSuchElementException e) {
			log.info("Customer {} or {} not found", newTransfer.getSenderUsername(), newTransfer.getReceiverUsername());
			return "redirect:/transfer";
		} catch (IllegalArgumentException e) {
			log.info("Invalid transfer request: {}", e.getMessage());
			return "redirect:/transfer";
		}
	}

	/**
	 * Checks if the customer is currently logged in.
	 *
	 * @param request the HTTP request object
	 * @return true if the customer is logged in, false otherwise
	 */
	private boolean isCustomerLoggedIn(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return session != null && session.getAttribute("email") != null;
	}

}
