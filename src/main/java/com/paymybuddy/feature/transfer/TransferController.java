package com.paymybuddy.feature.transfer;

import com.paymybuddy.core.security.AuthenticationService;
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

/**
 * Controller for managing money transfers from one customer to another.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class TransferController {

	private final AuthenticationService authService;
	private final TransferService transferService;

	/**
	 * Displays the transfer page.
	 *
	 * @param model   the model holding view attributes
	 * @param session the HTTP session of the logged-in customer
	 * @param request the HTTP request object
	 * @return the name of the view to render or a redirect to the login page if not logged in
	 */
	@GetMapping("/transfer")
	public String displayTransferPage(Model model, HttpSession session, HttpServletRequest request) {
		if (!authService.isCustomerLoggedIn(request)) {
			log.warn("Customer is not logged in, redirect to login page");
			return "redirect:/login";
		}

		var username = session.getAttribute("username").toString();
		Set<Customer> buddies = transferService.getBuddiesForCustomer(username);
		List<Transfer> transfers = transferService.getTransfersForCustomer(username);

		model.addAttribute("newTransfer", TransferDTO.builder().build());
		model.addAttribute("buddies", buddies);
		model.addAttribute("transfers", transfers);
		log.info("Displaying transfer page");
		return "redirect:/transfer";
	}

	/**
	 * Handles the creation of a new transfer.
	 *
	 * @param dto               the transfer data
	 * @param result            binding result for validation errors
	 * @param redirectAttributes used to pass messages (success or error) to the redirected view
	 * @param request           the HTTP request object
	 * @param session           the HTTP session of the logged-in customer
	 * @return a redirect to the transfer page with success or error messages
	 */
	@PostMapping("/transfer")
	public String createTransfer(@ModelAttribute @Validated TransferDTO dto,
								 BindingResult result, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpSession session) {
		if (!authService.isCustomerLoggedIn(request)) {
			log.warn("Customer is not logged in, redirect to login page");
			return "redirect:/login";
		}

		var username = session.getAttribute("username").toString();

		log.info("Transfer attempt from {} to {}", username, dto.getReceiverUsername());

		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Erreur dans les champs du formulaire.");
		}

		try {
			transferService.createTransfer(username, dto.getReceiverUsername(), dto.getAmount(), dto.getDescription());
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

}
