package com.paymybuddy.feature.transfer;

import com.paymybuddy.feature.customer.Customer;
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
import java.util.Set;

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
		Set<Customer> buddies = service.getBuddiesForCustomer(username);
		List<Transfer> transfers = service.getTransfersForCustomer(username);

		model.addAttribute("newTransfer", TransferDTO.builder().build());
		model.addAttribute("buddies", buddies);
		model.addAttribute("transfers", transfers);
		log.info("Displaying transfer page");
		return "transfer";
	}

	/**
	 * TODO: Pas encore opérationnel
	 *
	 * @param dto
	 * @param result
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/transfer")
	public String createTransfer(@ModelAttribute @Validated TransferDTO dto, BindingResult result, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpSession session) {
		if (!isCustomerLoggedIn(request)) {
			log.warn("Customer is not logged in, redirect to login page");
			return "redirect:/login";
		}

		var username = session.getAttribute("username").toString();

		log.info("Transfer attempt from {} to {}", username, dto.getReceiverUsername());

		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Erreur dans les champs du formulaire.");
			return "redirect:/transfer";
		}

		try {
			service.createTransfer(username, dto.getReceiverUsername(), dto.getAmount(), dto.getDescription());
			redirectAttributes.addFlashAttribute("successMessage", "Transfert effectué");
			log.info("{} sent {}€ to {}", username, dto.getAmount(), dto.getReceiverUsername());
		} catch (NoSuchElementException e) {
			log.info("Customer {} or {} not found", username, dto.getReceiverUsername());
			redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur non trouvé.");
		} catch (IllegalArgumentException e) {
			log.info("Invalid transfer request: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("errorMessage", "Demande de transfert erronée");
		}

		return "redirect:/transfer";
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
