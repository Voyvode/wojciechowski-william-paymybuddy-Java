package com.paymybuddy.feature.customer;

import com.paymybuddy.core.exceptions.CustomerNotFoundException;
import com.paymybuddy.core.exceptions.EmailAlreadyExistsException;
import com.paymybuddy.core.exceptions.UsernameAlreadyExistsException;
import com.paymybuddy.feature.customer.dto.CustomerDTO;
import com.paymybuddy.feature.customer.dto.NewCustomerDTO;
import com.paymybuddy.feature.customer.dto.UpdatedCustomerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * REST controller for managing customers.
 */
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/api/customers")
public class CustomerController {

	private final CustomerService service;

	/**
	 * Registers a new customer.
	 *
	 * @param newCustomer the new customer to be registered
	 * @return ResponseEntity with status CREATED if successful, or CONFLICT if the email or the
	 *         username is already used
	 */
	@PostMapping
	public ResponseEntity<Void> create(@RequestBody NewCustomerDTO newCustomer) {
		if (service.register(newCustomer.getUsername(), newCustomer.getEmail(), newCustomer.getPassword())) {
			log.info("New customer '{}' registered with email '{}'", newCustomer.getUsername(), newCustomer.getEmail());
			return ResponseEntity.status(CREATED).build();
		} else {
			log.info("Username '{}' or email '{}' already used", newCustomer.getUsername(), newCustomer.getEmail());
			return ResponseEntity.status(CONFLICT).build();
		}
	}

	/**
	 * Gets a customer.
	 *
	 * @param customer the registered customer to retrieve
	 * @return ResponseEntity with status CREATED if successful, or CONFLICT if the email or the
	 *         username is already used
	 */
	@GetMapping
	public ResponseEntity<Void> read(@RequestBody CustomerDTO customer) throws AuthenticationException {
		if (service.authenticate(customer.getEmail(), customer.getPassword())) {
			log.info("Customer {} sign in", customer.getEmail());
			return ResponseEntity.status(CREATED).build();
		} else {
			log.info("Customer {} doesn't exist or wrong password", customer.getEmail());
			return ResponseEntity.status(CONFLICT).build();
		}
	}

	/**
	 * Updates a customer's profile.
	 *
	 * @param customerId the ID of the customer to update
	 * @param updatedCustomer the updated customer information
	 * @return ResponseEntity with status OK if successful, NOT_FOUND if the customer doesn't exist,
	 *         or CONFLICT if the new email or username is already in use
	 */
	@PutMapping("/{customerId}")
	public ResponseEntity<Void> updateProfile(@PathVariable Long customerId, @RequestBody UpdatedCustomerDTO updatedCustomer)
			throws UsernameAlreadyExistsException, EmailAlreadyExistsException, CustomerNotFoundException {
		try {
			System.out.println(customerId);
			boolean updated = service.updateCustomer(customerId, updatedCustomer);
			if (updated) {
				log.info("Customer {} updated successfully", customerId);
				return ResponseEntity.ok().build();
			} else {
				log.info("Customer {} not found", customerId);
				return ResponseEntity.notFound().build();
			}
		} catch (IllegalArgumentException e) {
			log.info("Conflict while updating customer {}: {}", customerId, e.getMessage());
			return ResponseEntity.status(CONFLICT).build();
		}
	}

}
