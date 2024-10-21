package com.paymybuddy.feature.buddy;

import com.paymybuddy.core.exceptions.CustomerNotFoundException;
import com.paymybuddy.feature.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuddyService {

	private final BuddyRepository buddyRepo;
	private final CustomerRepository customerRepo;

	public void addBuddy(Long customerId, Long otherCustomerId) throws CustomerNotFoundException, IllegalArgumentException {
		if (!customerRepo.existsById(customerId)) {
			throw new CustomerNotFoundException("Customer not found");
		}
		if (!customerRepo.existsById(otherCustomerId)) {
			throw new CustomerNotFoundException("Buddy not found");
		}

		if (customerId.equals(otherCustomerId)) {
			throw new IllegalArgumentException("A customer cannot add themselves as a buddy");
		}

		BuddyId buddyId = new BuddyId();
		buddyId.setCustomerId(customerId);
		buddyId.setBuddyId(otherCustomerId);

		if (buddyRepo.existsById(buddyId)) {
			throw new IllegalArgumentException("Buddy already in the list");
		}

		Buddy buddy = new Buddy();
		buddy.setId(buddyId);
		buddyRepo.save(buddy);
	}

	public List<String> getBuddies(Long customerId) throws CustomerNotFoundException {
		if (!customerRepo.existsById(customerId)) {
			throw new CustomerNotFoundException("Customer with id " + customerId + " not found");
		}

		return buddyRepo.findBuddyUsernamesByCustomerId(customerId);
	}

}
