package com.paymybuddy.feature.transfer;

import com.paymybuddy.feature.customer.Customer;
import com.paymybuddy.feature.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TransferService {

	private final TransferRepository transferRepo;
	private final CustomerRepository customerRepo;

	public void createTransfer(String senderUsername, String receiverUsername, BigDecimal amount, String description) {
		var sender = customerRepo.findByUsername(senderUsername)
				.orElseThrow(() -> new NoSuchElementException("Sender not found"));
		var receiver = customerRepo.findByUsername(receiverUsername)
				.orElseThrow(() -> new NoSuchElementException("Receiver not found"));

		var transfer = new Transfer(sender, receiver, amount, description);

		transferRepo.save(transfer);
	}

	public Set<Customer> getBuddiesForCustomer(String customerUsername) {
		if (!customerRepo.existsByUsername(customerUsername)) {
			throw new NoSuchElementException("Customer with id " + customerUsername + " not found");
		}

		return customerRepo.findBuddiesByCustomerUsername(customerUsername);
	}

	public List<Transfer> getTransfersForCustomer(String customerUsername) {
		if (!customerRepo.existsByUsername(customerUsername)) {
			throw new NoSuchElementException("Customer with id " + customerUsername + " not found");
		}

		return transferRepo.findAllTransfersForCustomer(customerUsername);
	}
}
