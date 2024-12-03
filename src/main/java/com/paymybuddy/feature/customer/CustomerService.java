package com.paymybuddy.feature.customer;

import com.paymybuddy.core.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Service for managing customer-related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
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
		log.debug("{} initiated password change", customerUsername);

		if (oldPassword == null || newPassword == null || newPassword.isBlank()) {
			log.error("{} entered blank password", customerUsername);
			throw new IllegalArgumentException("Password must not be null or blank");
		}

		var existingCustomer = customerRepo.findByUsername(customerUsername)
				.orElseThrow(() -> {
					log.error("Customer {} not found", customerUsername);
					return new NoSuchElementException("Customer not found with username: " + customerUsername);
				});

		if (!SecurityUtils.verifyPassword(oldPassword, existingCustomer.getPasswordHash())) {
			log.warn("Old password does not match for {}", customerUsername);
			throw new IllegalArgumentException("L’ancien mot de passe est incorrect");
		}

		if (!SecurityUtils.isPasswordStrong(newPassword)) {
			log.warn("New password provided is not strong enough for {}", customerUsername);
			throw new IllegalArgumentException("Force du nouveau mot de passe insuffisante");
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
		log.debug("User {} is attempting to add buddy with email: {}", customerUsername, buddyEmail);

		var customer = customerRepo.findByUsername(customerUsername)
				.orElseThrow(() -> {
					log.error("Customer {} not found", customerUsername);
					return new NoSuchElementException("Customer not found");
				});

		var buddy = customerRepo.findByEmail(buddyEmail)
				.orElseThrow(() -> {
					log.error("Buddy not found with email {}", buddyEmail);
					return new NoSuchElementException("Buddy not found");
				});

		if (customer.getId().equals(buddy.getId())) {
			log.error("{} tried to be buddy its own buddy?!", customerUsername);
			throw new IllegalArgumentException("A customer can't be its own buddy!");
		}

		if (customer.getBuddies().contains(buddy)) {
			log.info("Buddy with email {} is already added to {} buddy list", buddyEmail, customerUsername);
			throw new IllegalArgumentException("This buddy is already there");
		}

		customer.addBuddy(buddy);
		customerRepo.save(customer);
		log.info("{} added {} to its buddy list", buddyEmail, customerUsername);

		return buddy.getUsername();
	}

}
