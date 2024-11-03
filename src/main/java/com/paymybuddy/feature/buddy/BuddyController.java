package com.paymybuddy.feature.buddy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

	/**
	 * Displays the field to add a buddy.
	 *
	 * <p>Redirects to the login page if the customer is not logged in.
	 *
	 * @param model the model to hold attributes for the view
	 * @param request the HTTP request object
	 * @return the name of the view to render
	 */
	@GetMapping("/add-buddy")
	public String displayProfile(Model model, HttpServletRequest request) {
		if (!isCustomerLoggedIn(request)) {
			return "redirect:/login";
		}

		HttpSession session = request.getSession(false);
		model.addAttribute("username", session.getAttribute("username"));
		model.addAttribute("email", session.getAttribute("email"));
		log.info("Displaying add page for customer: {}", session.getAttribute("email"));
		return "add-buddy";
	}


	/**
	 * // TODO: Pas encore opérationnel
	 * @param customerId
	 * @param buddyId
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Void> addBuddy(@RequestParam Long customerId, @RequestParam Long buddyId) { // TODO: virer ResponseEntity
		try {
			service.addBuddy(customerId, buddyId);
			log.info("Customer {} add {} successfully", customerId, buddyId);
			return ResponseEntity.ok().build();
//		} catch (CustomerNotFoundException e) {
//			log.info("Customer {} or {} not found", customerId, buddyId);
//			return ResponseEntity.notFound().build();
		} catch (IllegalArgumentException e) {
			log.info("Customer {} already added {}", customerId, e.getMessage());
			return ResponseEntity.status(CONFLICT).build();
		}
	}

	/**
	 * Checks if a customer is currently logged in.
	 *
	 * @param request the HTTP request object
	 * @return true if the customer is logged in, false otherwise
	 */
	private boolean isCustomerLoggedIn(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return session != null && session.getAttribute("email") != null;
	}

}
