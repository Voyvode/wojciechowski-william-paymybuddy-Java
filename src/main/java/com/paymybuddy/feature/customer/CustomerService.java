package com.paymybuddy.feature.customer;

import com.paymybuddy.core.exceptions.UsernameNotFoundException;
import com.paymybuddy.core.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

	private final CustomerRepository repository;
	private final SecurityService security;

	@Autowired
	public CustomerService(CustomerRepository repository, SecurityService security) {
		this.repository = repository;
		this.security = security;
	}

	public boolean register(String username, String email, String password) {
		if (repository.existsByEmailNot(username) && repository.existsByUsernameNot(username)) {
			var newCustomer = new Customer(username, email, security.hashPassword(password));
			repository.save(newCustomer);
			return true;
		} else {
			return false;
		}
	}

	public boolean authenticate(String username, String password) throws UsernameNotFoundException {
		Customer customer = repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return security.verifyPassword(password, customer.getPasswordHash());
	}

}
