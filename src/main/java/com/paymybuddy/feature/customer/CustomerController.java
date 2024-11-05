package com.paymybuddy.feature.customer;

import jakarta.persistence.EntityExistsException;
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

import java.util.NoSuchElementException;

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
	 * @param dto the new customer information
	 * @param result the binding result for validation
	 * @param redirectAttributes the redirect attributes for flash messages
	 * @return the name of the view to render
	 */
	@PostMapping("/register")
	public String register(@ModelAttribute @Validated CustomerDTO dto, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			log.error("Registration form has errors: {}", result.getAllErrors());
			return "register";
		}

		try {
			service.register(dto.getUsername(), dto.getEmail(), dto.getPassword());
			log.info("New customer '{}' registered with email '{}'", dto.getUsername(), dto.getEmail());
			redirectAttributes.addFlashAttribute("message", "Registration successful, now please login");
			return "redirect:/login";
		} catch (EntityExistsException e) {
			log.warn("Username '{}' or email '{}' already used", dto.getUsername(), dto.getEmail());
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
	@PostMapping("/profile")
	public String changePassword(String newPassword, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		if (isCustomerLoggedIn(request)) {
			log.warn("Update password failed: customer not logged in.");
			return "redirect:/login";
		}

		HttpSession session = request.getSession(false);
		var customerUsername = session.getAttribute("username").toString();
		try {
			service.changePassword(customerUsername, newPassword);
			log.info("Password for customer {} updated successfully", customerUsername);
			redirectAttributes.addFlashAttribute("message", "Password updated successfully");
			return "redirect:/profile";
		} catch(NoSuchElementException e) {
			log.error("Failed to update password for customer {}", customerUsername);
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
	 *
	 * @return
	 */
	@PostMapping("/add")
	public String addBuddy(String buddyEmail, HttpSession session) {
		var customerUsername = session.getAttribute("username").toString();
		var buddyUsername = service.customerAddBuddy(customerUsername, buddyEmail); // TODO: clarifier
		log.info("{} has added {} to buddy list", customerUsername, buddyUsername);
		return "transfer";
	}

	/**
	 * Checks if a customer is currently logged in.
	 *
	 * @param request the HTTP request object
	 * @return true if the customer is logged in, false otherwise
	 */
	private boolean isCustomerLoggedIn(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return session != null && session.getAttribute("username") != null;
	}

}
