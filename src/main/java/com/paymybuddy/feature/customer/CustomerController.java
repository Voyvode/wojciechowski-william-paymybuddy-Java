package com.paymybuddy.feature.customer;

import com.paymybuddy.core.security.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

/**
 * Controller for managing customer-related operations.
 *
 * <p>Handles profile viewing, password changes, and buddy management.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class CustomerController {

	private final AuthenticationService authService;
	private final CustomerService customerService;

	/**
	 * Displays the customer's profile.
	 *
	 * @param model   the model for view attributes
	 * @param request the HTTP request for session management
	 * @return the view name or login redirect
	 */
	@GetMapping("/profile")
	public String displayProfile(Model model, HttpServletRequest request) {
		if (!authService.isCustomerLoggedIn(request)) {
			return "redirect:/login";
		}

		var session = request.getSession(false);
		model.addAttribute("username", session.getAttribute("username"));
		model.addAttribute("email", session.getAttribute("email"));
		log.info("Displaying profile for '{}'", session.getAttribute("username"));
		return "profile";
	}

	/**
	 * Changes the customer's password.
	 *
	 * @param oldPassword        the current password
	 * @param newPassword        the new password
	 * @param request            the HTTP request for session management
	 * @param redirectAttributes the attributes for flash messages
	 * @param model              the model for view attributes
	 * @return redirect URL
	 */
	@PostMapping("/profile/change-password")
	public String changePassword(@Valid @RequestParam String oldPassword,
								 @Valid @RequestParam String newPassword,
								 HttpServletRequest request,
								 RedirectAttributes redirectAttributes, Model model) {
		if (!authService.isCustomerLoggedIn(request)) {
			log.warn("Update password failed: customer not logged in.");
			return "redirect:/login";
		}

		var session = request.getSession(false);
		var customerUsername = session.getAttribute("username").toString();

		try {
			customerService.changePassword(customerUsername, oldPassword, newPassword);
			log.info("Password for customer {} updated successfully", customerUsername);
			redirectAttributes.addFlashAttribute("message", "Password updated successfully");
		} catch (NoSuchElementException e) {
			log.error("Failed to update password for customer {}", customerUsername);
			model.addAttribute("error", "Mot de passe incorrect. Réessayez.");
			redirectAttributes.addFlashAttribute("error", "Failed to update password. Please try again.");
		}
		return "redirect:/profile";
	}

	/**
	 * Displays the add buddy form.
	 *
	 * @param request the HTTP request for session management
	 * @param model   the model for view attributes
	 * @return view name or login redirect
	 */
	@GetMapping("/add")
	public String displayAdd(HttpServletRequest request, Model model) {
		if (!authService.isCustomerLoggedIn(request)) {
			return "redirect:/login";
		}

		var session = request.getSession(false);
		model.addAttribute("username", session.getAttribute("username"));
		model.addAttribute("email", session.getAttribute("email"));
		log.info("Displaying add page for customer: {}", session.getAttribute("email"));
		return "add";
	}

	/**
	 * Adds a buddy to the customer's buddy list.
	 *
	 * @param buddyEmail         the email of the buddy to add
	 * @param request            the HTTP request for session management
	 * @param redirectAttributes the attribues for flash messages
	 * @return the URL redirect
	 */
	@PostMapping("/add")
	public String addBuddy(@Valid @Email @RequestParam String buddyEmail,
						   HttpServletRequest request,
						   RedirectAttributes redirectAttributes) {
		if (!authService.isCustomerLoggedIn(request)) {
			return "redirect:/login";
		}

		var session = request.getSession(false);
		var customerUsername = session.getAttribute("username").toString();

		try {
			var buddyUsername = customerService.addBuddy(customerUsername, buddyEmail);
			log.info("{} has added {} to buddy list", customerUsername, buddyUsername);
			redirectAttributes.addFlashAttribute("message", "Contact added successfully");
		} catch (Exception e) {
			log.error("Failed to add buddy for {}: {}", customerUsername, e.getMessage());
			redirectAttributes.addFlashAttribute("error", "Failed to add contact: " + e.getMessage());
		}

		return "redirect:/transfer";
	}

}
