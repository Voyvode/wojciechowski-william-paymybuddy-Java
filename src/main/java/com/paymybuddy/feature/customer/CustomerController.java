package com.paymybuddy.feature.customer;

import com.paymybuddy.core.exceptions.CustomerNotFoundException;
import com.paymybuddy.core.exceptions.EmailAlreadyExistsException;
import com.paymybuddy.core.exceptions.UsernameAlreadyExistsException;
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
			return "redirect:/login"; //TODO: Rediriger vers transfer
		} else {
			log.info("Username '{}' or email '{}' already used", newCustomer.getUsername(), newCustomer.getEmail());
			result.rejectValue("username", "error.user", "Username or email already in use");
			return "register";
		}
	}

	// TODO: Méthode temporaire pour afficher le formulaire de connexion sans Spring Security
	@GetMapping("/login")
	public String showLoginForm(Model model) {
		model.addAttribute("customer", CustomerDTO.builder().build());
		return "login";
	}

	// TODO: Méthode temporaire pour traiter la connexion Spring Security
	@PostMapping("/login")
	public String login(@ModelAttribute CustomerDTO customerDTO, Model model) throws AuthenticationException {
		Customer authenticatedCustomer = service.authenticate(customerDTO.getEmail(), customerDTO.getPassword());

		if (authenticatedCustomer != null) {
			model.addAttribute("customer", authenticatedCustomer);
			return "profile"; // Affiche la page de profil avec les infos du client
		} else {
			model.addAttribute("error", "Invalid username or password");
			return "login"; // Retourne au formulaire de connexion en cas d'erreur
		}
	}

	/**
	 * Displays the customer's profile.
	 */
	@GetMapping("/profile")
	public String showProfile(Model model, HttpSession session) {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null) {
			return "redirect:/login";
		}
		model.addAttribute("customer", customer);
		return "profile";
	}

//	/**
//	 * Authenticates a customer.
//	 *
//	 * @param customer the registered customer to retrieve
//	 * @return true if authentication was successful, false otherwise
//	 * @throws AuthenticationException if authentication fails
//	 */
//	public boolean authenticate(@RequestBody CustomerDTO customer) throws AuthenticationException {
//		boolean authenticated = service.authenticate(customer.getEmail(), customer.getPassword());
//		if (authenticated) {
//			log.info("Customer {} sign in", customer.getEmail());
//		} else {
//			log.info("Customer {} doesn't exist or wrong password", customer.getEmail());
//		}
//
//		return authenticated;
//	}

	/**
	 * Updates a customer's information.
	 *
	 * @param customerId the ID of the customer to update
	 * @param updatedCustomer the updated customer information
	 * @return true if update was successful, false if customer not found
	 * @throws UsernameAlreadyExistsException if the new username is already in use
	 * @throws EmailAlreadyExistsException if the new email is already in use
	 * @throws CustomerNotFoundException if the customer is not found
	 */
	public boolean update(@PathVariable Long customerId, @RequestBody CustomerDTO updatedCustomer)
			throws UsernameAlreadyExistsException, EmailAlreadyExistsException, CustomerNotFoundException {
		boolean updated = service.update(customerId, updatedCustomer);
		if (updated) {
			log.info("Customer {} updated successfully", customerId);
		} else {
			log.info("Customer {} not found", customerId);
		}

		return updated;
	}

}

