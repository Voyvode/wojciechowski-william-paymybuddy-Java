package com.paymybuddy.feature.customer;

import com.paymybuddy.core.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepo;

	public void changePassword(String customerUsername, String oldPassword, String newPassword) {
		if (oldPassword == null || newPassword == null || newPassword.isBlank()) {
			throw new IllegalArgumentException("Passwords must not be null or blank");
		}

		var existingCustomer = customerRepo.findByUsername(customerUsername)
				.orElseThrow(() -> new NoSuchElementException("Customer not found with username: " + customerUsername));

		if (!SecurityUtils.verifyPassword(oldPassword, existingCustomer.getPasswordHash())) {
			throw new IllegalArgumentException("Old password does not match");
		}

		var newPasswordHash = SecurityUtils.hashPassword(newPassword);
		existingCustomer.setPasswordHash(newPasswordHash);
		customerRepo.save(existingCustomer);
	}

	public String addBuddy(String customerUsername, String buddyEmail) {
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
