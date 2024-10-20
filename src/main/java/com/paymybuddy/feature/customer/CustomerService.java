package com.paymybuddy.feature.customer;

import com.paymybuddy.core.exceptions.CustomerNotFoundException;
import com.paymybuddy.core.exceptions.EmailAlreadyExistsException;
import com.paymybuddy.core.exceptions.UsernameAlreadyExistsException;
import com.paymybuddy.core.security.SecurityService;
import com.paymybuddy.feature.customer.dto.UpdatedCustomerDTO;
import jakarta.persistence.EntityExistsException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

	private final CustomerRepository repository;
	private final SecurityService security;

	public CustomerService(CustomerRepository repository, SecurityService security) {
		this.repository = repository;
		this.security = security;
	}

	public boolean register(String username, String email, String password) throws EntityExistsException {
		repository.findByUsernameOrEmail(username, email)
				.ifPresent(customer -> {
					throw new EntityExistsException("Customer with same username or email already exists");
				});

		var newCustomer = new Customer(username, email, security.hashPassword(password));
		repository.save(newCustomer);

		return true;
	}

	public boolean authenticate(String email, String password) throws AuthenticationException {
		repository.findByEmail(email)
				.map(customer -> security.verifyPassword(password, customer.getPasswordHash()))
				.orElseThrow(() -> new AuthenticationException("User not found"));

		return true;
	}

	public boolean updateCustomer(Long customerId, UpdatedCustomerDTO updatedCustomer) throws CustomerNotFoundException, UsernameAlreadyExistsException, EmailAlreadyExistsException {
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
