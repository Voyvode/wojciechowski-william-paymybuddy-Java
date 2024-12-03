package com.paymybuddy.feature.transfer;

import com.paymybuddy.feature.customer.Customer;
import com.paymybuddy.feature.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Service for managing money transfers from one customer to another.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

	private final TransferRepository transferRepo;
	private final CustomerRepository customerRepo;

	/**
	 * Creates a new transfer from the sender to the receiver.
	 *
	 * @param senderUsername the username of the sender
	 * @param receiverUsername the username of the receiver
	 * @param amount the amount to be transferred
	 * @param description a description of the transfer
	 */
	@Transactional
	public void createTransfer(String senderUsername, String receiverUsername, BigDecimal amount, String description) {
		log.debug("Creating transfer from {} to {} for amount: {}", senderUsername, receiverUsername, amount);

		var sender = customerRepo.findByUsername(senderUsername)
				.orElseThrow(() -> {
					log.error("Sender not found with username: {}", senderUsername);
					return new NoSuchElementException("Sender not found");
				});
		var receiver = customerRepo.findByUsername(receiverUsername)
				.orElseThrow(() -> {
					log.error("Receiver not found with username {}", receiverUsername);
					return new NoSuchElementException("Receiver not found");
				});

		var transfer = new Transfer(sender, receiver, amount, description);

		transferRepo.save(transfer);

		log.info("Transfer created successfully from {} to {} for amount: {}", senderUsername, receiverUsername, amount);
	}

	/**
	 * Retrieves the buddy list for a customer.
	 *
	 * @param customerUsername the username of the customer
	 * @return a set of buddies for the specified customer
	 */
	public Set<Customer> getBuddiesForCustomer(String customerUsername) {
		if (customerRepo.isUsernameAvailable(customerUsername)) {
			throw new NoSuchElementException("Customer with id " + customerUsername + " not found");
		}

		return customerRepo.findBuddiesByCustomerUsername(customerUsername);
	}

	/**
	 * Retrieves all transfers made by a customer.
	 *
	 * @param customerUsername the username of the customer
	 * @return a list of transfers made by the specified customer
	 */
	public List<Transfer> getTransfersForCustomer(String customerUsername) {
		if (customerRepo.isUsernameAvailable(customerUsername)) {
			throw new NoSuchElementException("Customer with id " + customerUsername + " not found");
		}

		return transferRepo.findAllTransfersForCustomer(customerUsername);
	}

}
