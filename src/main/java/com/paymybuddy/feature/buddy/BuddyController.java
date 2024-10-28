package com.paymybuddy.feature.buddy;

import com.paymybuddy.core.exceptions.CustomerNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.HttpStatus.CONFLICT;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
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

	@GetMapping("/search")
	public String showSearch() {
		return "search";
	}

//	@GetMapping
//	public ResponseEntity<List<String>> getBuddies(@RequestParam Long customerId) {
//		try {
//			List<String> buddies = service.getBuddies(customerId);
//			return ResponseEntity.ok(buddies);
//		} catch (CustomerNotFoundException e) {
//			log.info("Customer {} not found", customerId);
//			return ResponseEntity.notFound().build();
//		}
//	}

	private boolean isCustomerLoggedIn(HttpSession session) {
		return session.getAttribute("customer") != null;
	}

}
