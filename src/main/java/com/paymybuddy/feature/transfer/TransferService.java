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

	public void createTransfer(Long senderId, Long receiverId, BigDecimal amount, String description) throws CustomerNotFoundException {
		var transfer = new Transfer();
//		transfer.setSender(senderId);
//		transfer.setReceiver(receiverId);
		transfer.setAmount(amount);
		transfer.setDescription(description);

		transferRepo.save(transfer);
	}

	public List<Transfer> getTransfersForCustomer(String customerUsername) throws CustomerNotFoundException {
		if (!customerRepo.existsByUsername(customerUsername)) {
			throw new CustomerNotFoundException("Customer with id " + customerUsername + " not found");
		}

		return transferRepo.findAllTransfersForCustomer(customerUsername);
	}
}
