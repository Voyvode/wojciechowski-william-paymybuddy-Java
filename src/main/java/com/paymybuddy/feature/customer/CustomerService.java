package com.paymybuddy.feature.customer;

import com.paymybuddy.core.security.SecurityService;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository repository;
	private final SecurityService security;

	public boolean register(String username, String email, String password) throws EntityExistsException {
		repository.findByUsernameOrEmail(username, email)
				.ifPresent(customer -> {
					throw new EntityExistsException("Customer with same username or email already exists");
				});

		var newCustomer = new Customer(username, email, security.hashPassword(password));
		repository.save(newCustomer);

		return true;
	}

	public Customer authenticate(String email, String password) throws AuthenticationException {
		return repository.findByEmail(email)
				.filter(customer -> security.verifyPassword(password, customer.getPasswordHash()))
				.orElseThrow(() -> new AuthenticationException("User not found"));
	}

	public boolean changePassword(String customerEmail, String newPassword) {
		var existingCustomer = repository.findByEmail(customerEmail)
				.orElseThrow(() -> new NoSuchElementException("Customer not found with email: " + customerEmail));

		if (newPassword != null && !newPassword.isEmpty()) {
			var newPasswordHash = security.hashPassword(newPassword);
			existingCustomer.setPasswordHash(newPasswordHash);
		}

		repository.save(existingCustomer);

		return true;
	}

}
