package com.paymybuddy.core.security;

import com.paymybuddy.feature.customer.Customer;
import com.paymybuddy.feature.customer.CustomerRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final CustomerRepository customerRepo;

	public void register(String username, String email, String password) {
		if (customerRepo.existsByUsernameOrEmail(username, email)) {
			throw new EntityExistsException("Customer with same username or email already exists");
		}
		var newCustomer = new Customer(username, email, SecurityUtils.hashPassword(password));
		customerRepo.save(newCustomer);
	}

	public Customer authenticate(String email, String password) throws AuthenticationException {
		return customerRepo.findByEmail(email)
				.filter(customer -> SecurityUtils.verifyPassword(password, customer.getPasswordHash()))
				.orElseThrow(() -> new AuthenticationException("Invalid email or password"));
	}

}
