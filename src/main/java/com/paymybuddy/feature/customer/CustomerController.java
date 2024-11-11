package com.paymybuddy.feature.customer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

/**
 * Controller for managing customers.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class CustomerController {

	private final CustomerService customerService;

	/**
	 * Displays the customer's profile.
	 *
	 * <p>Redirects to the login page if the customer is not logged in.
	 *
	 * @param model the model to hold attributes for the view
	 * @param request the HTTP request object
	 * @return the name of the view to render
	 */
	@GetMapping("/profile")
	public String displayProfile(Model model, HttpServletRequest request) {
		if (!isCustomerLoggedIn(request)) {
			return "redirect:/login";
		}

		HttpSession session = request.getSession(false);
		model.addAttribute("username", session.getAttribute("username"));
		model.addAttribute("email", session.getAttribute("email"));
		log.info("Displaying profile for '{}'", session.getAttribute("username"));
		return "profile";
	}

	/**
	 * Changes the customer's password.
	 *
	 * @param newPassword the customer's new password
	 * @param redirectAttributes the redirect attributes for flash messages
	 * @return the redirect URL after updating
	 */
	@PostMapping("/profile/change-password")
	public String changePassword(String oldPassword, String newPassword, RedirectAttributes redirectAttributes, HttpServletRequest request, Model model) {
		if (!isCustomerLoggedIn(request)) {
			log.warn("Update password failed: customer not logged in.");
			return "redirect:/login";
		}

		HttpSession session = request.getSession(false);
		var customerUsername = session.getAttribute("username").toString();
		try {
			customerService.changePassword(customerUsername, oldPassword, newPassword);
			log.info("Password for customer {} updated successfully", customerUsername);
			redirectAttributes.addFlashAttribute("message", "Password updated successfully");
			return "redirect:/profile";
		} catch(NoSuchElementException e) {
			log.error("Failed to update password for customer {}", customerUsername);
			model.addAttribute("error", "Mot de passe incorrect. Réessayez.");
			redirectAttributes.addFlashAttribute("error", "Failed to update password. Please try again.");
			return "redirect:/profile";
		}
	}

	/**
	 * Displays the field to add another customer as a buddy to send money.
	 *
	 * <p>Redirects to the login page if the customer is not logged in.
	 *
	 * @param model the model to hold attributes for the view
	 * @param request the HTTP request object
	 * @return the name of the view to render
	 */
	@GetMapping("/add")
	public String displayAdd(Model model, HttpServletRequest request) {
		if (!isCustomerLoggedIn(request)) {
			return "redirect:/login";
		}

		HttpSession session = request.getSession(false);
		model.addAttribute("username", session.getAttribute("username"));
		model.addAttribute("email", session.getAttribute("email"));
		log.info("Displaying add page for customer: {}", session.getAttribute("email"));
		return "add";
	}

	/**
	 * Adds a buddy (contact) to the customer's buddy list
	 *
	 * @param buddyEmail the email of the contact to be added
	 * @param session the HTTP session containing the logged-in customer's information
	 * @param redirectAttributes used to pass messages (success or error) to the redirected view
	 * @return a redirect to the transfer page
	 */
	@PostMapping("/add")
	public String addBuddy(String buddyEmail, HttpSession session, RedirectAttributes redirectAttributes) {
		var customerUsername = session.getAttribute("username").toString();

		try {
			var buddyUsername = customerService.addBuddy(customerUsername, buddyEmail);
			log.info("{} has added {} to buddy list", customerUsername, buddyUsername);
		} catch (Exception e) {
			log.error("Failed to add buddy for {}: {}", customerUsername, e.getMessage());
			redirectAttributes.addFlashAttribute("error", "Failed to add contact: " + e.getMessage());
		}

		return "redirect:/transfer";
	}

	/**
	 * Checks if a customer is currently logged in.
	 *
	 * @param request the HTTP request object
	 * @return true if the customer is logged in, false otherwise
	 */
	private boolean isCustomerLoggedIn(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return session != null && session.getAttribute("email") != null;
	}

}
