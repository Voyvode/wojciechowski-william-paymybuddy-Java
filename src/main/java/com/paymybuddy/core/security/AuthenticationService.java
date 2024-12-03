package com.paymybuddy.core.security;

import com.paymybuddy.feature.customer.Customer;
import com.paymybuddy.feature.customer.CustomerRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service handling user authentication and registration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

	private final CustomerRepository customerRepo;

	/**
	 * Registers a new customer.
	 *
	 * @param username the customer's username
	 * @param email    the customer's email
	 * @param password the customer's password
	 * @throws EntityExistsException if a customer with the same username or email already exists
	 */
	@Transactional
	public void register(String username, String email, String password) {
		log.debug("Attempting registration for username: {} with email: {}", username, email);

		if (customerRepo.existsByUsernameOrEmail(username, email)) {
			log.warn("Registration attempt with existing username ({}) or email ({})", username, email);
			throw new EntityExistsException("Customer with same username or email already exists");
		}
		if (!SecurityUtils.isPasswordStrong(password)) {
			log.warn("Registration attempt with weak password for {}", email);
			throw new IllegalArgumentException("Password is not strong enough");
		}
		var newCustomer = new Customer(username, email, SecurityUtils.hashPassword(password));
		customerRepo.save(newCustomer);

		log.info("New customer '{}' registered with {}", username, email);
	}

	/**
	 * Authenticates a customer.
	 *
	 * @param email    the customer's email
	 * @param password the customer's password
	 * @return the authenticated Customer
	 * @throws AuthenticationException if authentication fails
	 */
	public Customer authenticate(String email, String password) throws AuthenticationException {
		log.debug("Authenticating customer with email: {}", email);

		var customer = customerRepo.findByEmail(email)
				.filter(registeredCustomer -> SecurityUtils.verifyPassword(password, registeredCustomer.getPasswordHash()))
				.orElseThrow(() -> {
					log.warn("Failed authentication attempt for {}", email);
					return new AuthenticationException("Invalid email or password");
				});

		var now = Instant.now();
		customer.setLastLoginAt(now);
		customerRepo.save(customer);

		log.info("{} authenticated at {}", customer.getUsername(), now);
		return customer;
	}

	/**
	 * Checks if a customer is currently logged in.
	 *
	 * @param request the HTTP request object
	 * @return true if the customer is logged in, false otherwise
	 */
	public boolean isCustomerLoggedIn(HttpServletRequest request) {
		var session = request.getSession(false);
		return session != null && session.getAttribute("email") != null;
	}

}
