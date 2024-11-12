package com.paymybuddy.feature.customer;

import com.paymybuddy.core.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Service for managing customer-related operations.
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepo;

	/**
	 * Changes the password for a customer.
	 *
	 * @param customerUsername the username of the customer
	 * @param oldPassword      the current password
	 * @param newPassword      the new password
	 * @throws NoSuchElementException   if the customer is not found
	 * @throws IllegalArgumentException if passwords are invalid or don't match
	 */
	@Transactional
	public void changePassword(String customerUsername, String oldPassword, String newPassword) {
		if (oldPassword == null || newPassword == null || newPassword.isBlank()) {
			throw new IllegalArgumentException("Passwords must not be null or blank");
		}

		var existingCustomer = customerRepo.findByUsername(customerUsername)
				.orElseThrow(() -> new NoSuchElementException("Customer not found with username: " + customerUsername));

		if (!SecurityUtils.verifyPassword(oldPassword, existingCustomer.getPasswordHash())) {
			throw new IllegalArgumentException("Old password does not match");
		}

		if (!SecurityUtils.isPasswordStrong(newPassword)) {
			throw new IllegalArgumentException("New password is not strong enough");
		}

		var newPasswordHash = SecurityUtils.hashPassword(newPassword);
		existingCustomer.setPasswordHash(newPasswordHash);
		customerRepo.save(existingCustomer);
	}

	/**
	 * Adds a buddy to a customer's buddy list.
	 *
	 * @param customerUsername the username of the customer adding a buddy
	 * @param buddyEmail       the email of the buddy to be added
	 * @return the username of the added buddy
	 * @throws NoSuchElementException   if either customer or buddy is not found
	 * @throws IllegalArgumentException if a customer tries to add themselves as a buddy
	 */
	@Transactional
	public String addBuddy(String customerUsername, String buddyEmail) {
		var customer = customerRepo.findByUsername(customerUsername)
				.orElseThrow(() -> new NoSuchElementException("Customer not found"));
		var buddy = customerRepo.findByEmail(buddyEmail)
				.orElseThrow(() -> new NoSuchElementException("Buddy not found"));

		if (customer.getId().equals(buddy.getId())) {
			throw new IllegalArgumentException("A customer can't be buddy with themselves");
		}

		if (customer.getBuddies().contains(buddy)) {
			throw new IllegalArgumentException("This buddy is already there");
		}

		customer.addBuddy(buddy);
		customerRepo.save(customer);

		return buddy.getUsername();
	}

}
