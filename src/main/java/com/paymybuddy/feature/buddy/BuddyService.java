package com.paymybuddy.feature.buddy;

import com.paymybuddy.feature.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BuddyService {

	private final BuddyRepository buddyRepo;
	private final CustomerRepository customerRepo;

	public void addBuddy(Long customerId, Long otherCustomerId) {
		if (!customerRepo.existsById(customerId)) {
			throw new IllegalArgumentException("Customer not found"); // TODO: Exception plus adaptée + contexte
		}
		if (!customerRepo.existsById(otherCustomerId)) { // TODO: Exception plus adaptée + contexte
			throw new IllegalArgumentException("Buddy not found");
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

	public List<String> getBuddies(Long customerId) { // TODO: Exception plus adaptée + contexte
		if (!customerRepo.existsById(customerId)) {
			throw new NoSuchElementException("Customer with id " + customerId + " not found");
		}

		return buddyRepo.findBuddyUsernamesByCustomerId(customerId);
	}

}
