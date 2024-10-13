package com.paymybuddy.feature.customer;

import com.paymybuddy.core.exceptions.UsernameNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
	 * @param username the new customer username
	 * @param email the new customer email
	 * @param password the new customer password
	 * @return ResponseEntity with status CREATED if successful, or CONFLICT if the email or the
	 *         username is already used
	 */
	@PostMapping
	public ResponseEntity<Void> create(@RequestBody @Valid String username,
									   @RequestBody @Valid String email,
									   @RequestBody @Valid String password) {
		if (service.register(username, email, password)) {
			log.info("New customer {} registered with email {}", username, email);
			return ResponseEntity.status(CREATED).build();
		} else {
			// TODO: log plus spécifique
			log.info("Username {} or email {} already used", username, email);
			return ResponseEntity.status(CONFLICT).build();
		}
	}

	/**
	 * Gets a customer.
	 *
	 * @param username the new customer to be registered
	 * @return ResponseEntity with status CREATED if successful, or CONFLICT if the email or the
	 *         username is already used
	 */
	@GetMapping
	public ResponseEntity<Void> read(@RequestBody String username, @RequestBody String password) throws UsernameNotFoundException {
		if (service.authenticate(username, password)) {
			log.info("Customer {} sign in", username);
			return ResponseEntity.status(CREATED).build();
		} else {
			log.info("Customer {} doesn't exist or wrong password", username);
			return ResponseEntity.status(CONFLICT).build();
		}
	}

// TODO: Mis à jour du profil
//	/**
//	 * Updates a customer.
//	 *
//	 * @param customer the customer to update
//	 * @param updatedCustomer the updated customer
//	 * @return ResponseEntity with status OK if successful, or NOT_FOUND if the customer
//	 *         doesn't exist
//	 */
//	@PutMapping("/{address}")
//	public ResponseEntity<Void> update(@PathVariable String email, @RequestBody @Valid Customer updatedCustomer) {
//		if (service.update(address, updatedCustomer)) {
//			log.info("{} is now assigned to station {}", address, updatedCustomer.getStation());
//			return ResponseEntity.ok().build();
//		} else {
//			log.error("{} is not assigned, nothing to update", address);
//			return ResponseEntity.notFound().build();
//		}
//	}

// REMARQUE: Aucun bouton SUPPRIMER UTILISATEUR dans la maquette
//	/**
//	 * Deletes a customer.
//	 *
//	 * @param address the assigned address to delete
//	 * @return ResponseEntity with status NO_CONTENT if successful, or NOT_FOUND if the
//	 *         address is not assigned
//	 */
//	@DeleteMapping("/{address}")
//	public ResponseEntity<Void> delete(@PathVariable("address") String address) {
//		var deletedCustomer = service.deleteFirestation(address);
//
//		if (deletedCustomer != null) {
//			log.info("{} is not assigned to station {} any more", address, deletedCustomer.getEmail());
//			return ResponseEntity.noContent().build();
//		} else {
//			log.error("{} is not assigned, nothing to delete", address);
//			return ResponseEntity.notFound().build();
//		}
//	}

}
