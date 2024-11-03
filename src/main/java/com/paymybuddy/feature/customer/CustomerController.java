package com.paymybuddy.feature.customer;

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

/**
 * Controller for managing customers.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class CustomerController {

	private final CustomerService service;

	/**
	 * Displays the registration form.
	 *
	 * @param model the model holding attributes for the view
	 * @return the name of the view to render
	 */
	@GetMapping("/register")
	public String displayRegistrationForm(Model model) {
		model.addAttribute("newCustomer", CustomerDTO.builder().build());
		return "register";
	}

	/**
	 * Processes the registration of a new customer.
	 *
	 * @param newCustomer the new customer information
	 * @param result the binding result for validation
	 * @param redirectAttributes the redirect attributes for flash messages
	 * @return the name of the view to render
	 */
	@PostMapping("/register")
	public String register(@ModelAttribute @Validated CustomerDTO newCustomer, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			log.error("Registration form has errors: {}", result.getAllErrors());
			return "register";
		}

		boolean registered = service.register(newCustomer.getUsername(), newCustomer.getEmail(), newCustomer.getPassword());
		if (registered) {
			log.info("New customer '{}' registered with email '{}'", newCustomer.getUsername(), newCustomer.getEmail());
			redirectAttributes.addFlashAttribute("message", "Registration successful, now please login");
			return "redirect:/login";
		} else {
			log.warn("Username '{}' or email '{}' already used", newCustomer.getUsername(), newCustomer.getEmail());
			result.rejectValue("username", "error.user", "Username or email already in use");
			return "register";
		}
	}

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
		log.info("Displaying profile for customer: {}", session.getAttribute("email"));
		return "profile";
	}

	/**
	 * Changes the customer's password.
	 *
	 * @param updatedCustomer the updated customer information containing the new password
	 * @param redirectAttributes the redirect attributes for flash messages
	 * @return the redirect URL after updating
	 */
	@PostMapping("/profile")
	public String changePassword(@ModelAttribute CustomerDTO updatedCustomer, RedirectAttributes redirectAttributes, HttpSession session, HttpServletRequest request) {
		if (isCustomerLoggedIn(request)) {
			log.warn("Update password failed: customer not logged in.");
			redirectAttributes.addFlashAttribute("error", "You must be logged in to update your password.");
			return "redirect:/login";
		}

		boolean updated = service.changePassword(updatedCustomer.getEmail(), updatedCustomer.getPassword()); // TODO: obtenir le nouveau MDP du formulaire
		if (updated) {
			log.info("Password for customer {} updated successfully", updatedCustomer.getEmail());
			redirectAttributes.addFlashAttribute("message", "Password updated successfully");
			return "redirect:/profile";
		} else {
			log.error("Failed to update password for customer {}", updatedCustomer.getEmail());
			redirectAttributes.addFlashAttribute("error", "Failed to update password. Please try again.");
			return "redirect:/profile";
		}
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
