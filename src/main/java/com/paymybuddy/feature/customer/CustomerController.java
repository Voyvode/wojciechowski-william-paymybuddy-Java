package com.paymybuddy.feature.customer;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
	 */
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("newCustomer", CustomerDTO.builder().build());
		return "register";
	}

	/**
	 * Processes the registration of a new customer.
	 */
	@PostMapping("/register")
	public String register(@ModelAttribute @Validated CustomerDTO newCustomer, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			log.error("Invalid form"); //TODO: log plus de détaillé
			return "register";
		}
		boolean registered = service.register(newCustomer.getUsername(), newCustomer.getEmail(), newCustomer.getPassword());
		if (registered) {
			log.info("New customer '{}' registered with email '{}'", newCustomer.getUsername(), newCustomer.getEmail());
			redirectAttributes.addFlashAttribute("message", "Registration successful");
			return "redirect:/transfer";
		} else {
			log.info("Username '{}' or email '{}' already used", newCustomer.getUsername(), newCustomer.getEmail());
			result.rejectValue("username", "error.user", "Username or email already in use");
			return "register";
		}
	}

	/**
	 * Displays the customer's profile.
	 */
	@GetMapping("/profile")
	public String showProfile(Model model, HttpServletRequest request) {
		if (!isCustomerLoggedIn(request)) {
			return "redirect:/login";
		}

		HttpSession session = request.getSession(false);
		model.addAttribute("customer", session.getAttribute("customer"));
		return "profile";
	}

	/**
	 * //TODO: À remanier
	 * Updates a customer's information.
	 *
	 * @param customerId the ID of the customer to update
	 * @param updatedCustomer the updated customer information
	 * @return true if update was successful, false if customer not found
	 */
	@PostMapping("/profile")
	public boolean update(@PathVariable Long customerId, @RequestBody CustomerDTO updatedCustomer) {
		boolean updated = service.update(customerId, updatedCustomer);
		if (updated) {
			log.info("Customer {} updated successfully", customerId);
		} else {
			log.info("Customer {} not found", customerId);
		}

		return updated;
	}

	private boolean isCustomerLoggedIn(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("JSESSIONID")) {
					HttpSession session = request.getSession(false);
					return session != null && session.getAttribute("customer") != null;
				}
			}
		}
		return false;
	}

}
