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

	private final CustomerRepository customerRepo;
	private final SecurityService securityService;

	public boolean register(String username, String email, String password) throws EntityExistsException {
		if (customerRepo.existsByUsernameOrEmail(username, email)) {
			throw new EntityExistsException("Customer with same username or email already exists");
		}
		var newCustomer = new Customer(username, email, securityService.hashPassword(password));
		customerRepo.save(newCustomer);
		return true;
	}

	// TODO: Mettre dans une nouvelle classe AuthService ? Fusionner avec SecurityService ?
	public Customer authenticate(String email, String password) throws AuthenticationException {
		return customerRepo.findByEmail(email)
				.filter(customer -> securityService.verifyPassword(password, customer.getPasswordHash()))
				.orElseThrow(() -> new AuthenticationException("Invalid email or password"));
	}

	public boolean changePassword(String customerUsername, String newPassword) {
		var existingCustomer = customerRepo.findByUsername(customerUsername)
				.orElseThrow(() -> new NoSuchElementException("Customer not found with username: " + customerUsername));
		var newPasswordHash = securityService.hashPassword(newPassword);
		existingCustomer.setPasswordHash(newPasswordHash);
		customerRepo.save(existingCustomer);
		return true;
	}

	public String customerAddBuddy(String customerUsername, String buddyEmail) {
		var customer = customerRepo.findByUsername(customerUsername)
				.orElseThrow(() -> new NoSuchElementException("Customer not found"));
		var buddy = customerRepo.findByEmail(buddyEmail)
				.orElseThrow(() -> new NoSuchElementException("Customer not found"));

		if (customer.getId().equals(buddy.getId())) {
			throw new IllegalArgumentException("A customer can't be buddy with himself!");
		}
		customer.addBuddy(buddy);
		customerRepo.save(customer);

		return buddy.getUsername();
	}

}
