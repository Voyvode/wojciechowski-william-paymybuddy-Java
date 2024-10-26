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

	TransferRepository transferRepo;
	CustomerRepository customerRepo;

	public void createTransfer(Long senderId, Long receiverId, BigDecimal amount, String description) throws CustomerNotFoundException {
		var transfer = new Transfer();
//		transfer.setSender(senderId);
//		transfer.setReceiver(receiverId);
		transfer.setAmount(amount);
		transfer.setDescription(description);

		transferRepo.save(transfer);
	}

	public List<Transfer> getTransfers(Long customerId) throws CustomerNotFoundException {
		if (!customerRepo.existsById(customerId)) {
			throw new CustomerNotFoundException("Customer with id " + customerId + " not found");
		}

//		return transferRepo.findAllBySender(customerId);
		return null;
	}
}
