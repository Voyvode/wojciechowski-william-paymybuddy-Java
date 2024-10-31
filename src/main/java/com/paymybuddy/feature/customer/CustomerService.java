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

	public boolean update(Long customerId, CustomerDTO updatedCustomer) {
		var existingCustomer = repository.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + customerId));

		if (updatedCustomer.getUsername() != null && !existingCustomer.getUsername().equals(updatedCustomer.getUsername())) {
			if (!repository.existsByUsername(updatedCustomer.getUsername())) {
				existingCustomer.setUsername(updatedCustomer.getUsername());
			} else {
				throw new IllegalArgumentException("Username already in use");
			}
		}

		if (updatedCustomer.getEmail() != null && !existingCustomer.getEmail().equals(updatedCustomer.getEmail())) {
			if (!repository.existsByEmail(updatedCustomer.getEmail())) {
				existingCustomer.setEmail(updatedCustomer.getEmail());
			} else {
				throw new IllegalArgumentException("Email already in use");
			}
		}

		if (updatedCustomer.getPassword() != null) {
			var newPasswordHash = security.hashPassword(updatedCustomer.getPassword());
			existingCustomer.setPasswordHash(newPasswordHash);
		}

		repository.save(existingCustomer);

		return true;
	}

}
