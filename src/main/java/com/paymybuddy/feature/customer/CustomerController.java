package com.paymybuddy.feature.customer;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Enumeration;

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

	// TODO: Méthode temporaire pour afficher le formulaire de connexion sans Spring Security
	@GetMapping("/login")
	public String showLoginForm(Model model, HttpSession session) {
		if (isCustomerLoggedIn(session)) {
			log.info("User '{}' already logged in, redirect", session.getAttribute("username"));
			return "redirect:/transfer";
		}

		model.addAttribute("customer", CustomerDTO.builder().build());
		log.info("Show login form");
		return "login";
	}

	// TODO: Méthode temporaire pour traiter la connexion sans Spring Security
	@PostMapping("/login")
	public String login(@ModelAttribute CustomerDTO customerDTO, Model model) throws AuthenticationException {
		log.info("Authentication try to {}", customerDTO.getEmail());
		Customer authenticatedCustomer = service.authenticate(customerDTO.getEmail(), customerDTO.getPassword());

		if (authenticatedCustomer != null) {
			model.addAttribute("customer", authenticatedCustomer);
			log.info("{} ({}) is authenticated, redirect to transfer", authenticatedCustomer.getEmail(), authenticatedCustomer.getUsername());
			return "redirect:/transfer"; // TODO: "redirect:/transfer" ou "transfer"
		} else {
			model.addAttribute("error", "Invalid username or password");
			return "login";
		}
	}

	/**
	 * Displays the customer's profile.
	 */
	@GetMapping("/profile")
	public String showProfile(Model model, HttpSession session) {
		session.setAttribute("email", "bernard@mail.com");// TODO: supprimer ce vilain traficotage de session
		session.setAttribute("username", "nanard");// TODO: supprimer ce vilain traficotage de session

		if (!isCustomerLoggedIn(session)) {
			return "redirect:/login";
		}

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

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

	private boolean isCustomerLoggedIn(HttpSession session) {
		Enumeration<String> attributeNames = session.getAttributeNames();

		System.out.println("Contenu de la session :");

		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			Object attributeValue = session.getAttribute(attributeName);
			System.out.println(attributeName + " : " + attributeValue);
		}
		return session.getAttribute("customer") != null;
	}

}
