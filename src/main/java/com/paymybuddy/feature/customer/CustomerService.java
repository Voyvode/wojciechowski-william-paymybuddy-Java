package com.paymybuddy.feature.customer;

import com.paymybuddy.core.exceptions.CustomerNotFoundException;
import com.paymybuddy.core.exceptions.EmailAlreadyExistsException;
import com.paymybuddy.core.exceptions.UsernameAlreadyExistsException;
import com.paymybuddy.core.security.SecurityService;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Service;

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

	public boolean update(Long customerId, CustomerDTO updatedCustomer) throws CustomerNotFoundException, UsernameAlreadyExistsException, EmailAlreadyExistsException {
		var existingCustomer = repository.findById(customerId)
				.orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));

		if (updatedCustomer.getUsername() != null && !existingCustomer.getUsername().equals(updatedCustomer.getUsername())) {
			if (!repository.existsByUsername(updatedCustomer.getUsername())) {
				existingCustomer.setUsername(updatedCustomer.getUsername());
			} else {
				throw new UsernameAlreadyExistsException("Username already in use");
			}
		}

		if (updatedCustomer.getEmail() != null && !existingCustomer.getEmail().equals(updatedCustomer.getEmail())) {
			if (!repository.existsByEmail(updatedCustomer.getEmail())) {
				existingCustomer.setEmail(updatedCustomer.getEmail());
			} else {
				throw new EmailAlreadyExistsException("Email already in use");
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
