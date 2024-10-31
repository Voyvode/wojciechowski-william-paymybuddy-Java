package com.paymybuddy.feature.transfer;

import com.paymybuddy.core.exceptions.CustomerNotFoundException;
import com.paymybuddy.feature.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {

	private final TransferRepository transferRepo;
	private final CustomerRepository customerRepo;

	public boolean createTransfer(String senderUsername, String receiverUsername, BigDecimal amount, String description) {
		var sender = customerRepo.findByUsername(senderUsername)
				.orElseThrow(() -> new NoSuchElementException("Sender not found"));
		var receiver = customerRepo.findByUsername(receiverUsername)
				.orElseThrow(() -> new NoSuchElementException("Receiver not found"));

		var transfer = new Transfer();
//		transfer.setSender(senderId);
//		transfer.setReceiver(receiverId);
		transfer.setAmount(amount);
		transfer.setDescription(description);

		transferRepo.save(transfer);
	}

	public List<Transfer> getTransfersForCustomer(String customerUsername) {
		if (!customerRepo.existsByUsername(customerUsername)) {
			throw new NoSuchElementException("Customer with id " + customerUsername + " not found");
		}

		return transferRepo.findAllTransfersForCustomer(customerUsername);
	}
}
