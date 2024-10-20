package com.paymybuddy.feature.buddy;

import com.paymybuddy.core.exceptions.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/api/buddies")
public class BuddyController {

	private final BuddyService service;

	@PostMapping
	public ResponseEntity<Void> addBuddy(@RequestParam Long customerId, @RequestParam Long buddyId) {
		try {
			service.addBuddy(customerId, buddyId);
			log.info("Customer {} add {} successfully", customerId, buddyId);
			return ResponseEntity.ok().build();
		} catch (CustomerNotFoundException e) {
			log.info("Customer {} or {} not found", customerId, buddyId);
			return ResponseEntity.notFound().build();
		} catch (IllegalArgumentException e) {
			log.info("Customer {} already added {}", customerId, e.getMessage());
			return ResponseEntity.status(CONFLICT).build();
		}
	}

	@GetMapping
	public ResponseEntity<List<String>> getBuddies(@RequestParam Long customerId) {
		try {
			List<String> buddies = service.getBuddies(customerId);
			return ResponseEntity.ok(buddies);
		} catch (CustomerNotFoundException e) {
			log.info("Customer {} not found", customerId);
			return ResponseEntity.notFound().build();
		}
	}

}
