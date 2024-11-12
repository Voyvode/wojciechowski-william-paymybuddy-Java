package com.paymybuddy.feature.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for Customer entity operations.
 *
 * <p>Provides methods for querying and managing Customer data.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	/**
	 * Checks if the given username is available (not already taken).
	 *
	 * @param username the username to check
	 * @return true if the username is available, false if it's already taken
	 */
	@Query("SELECT NOT EXISTS (SELECT 1 FROM Customer c WHERE c.username = :username)")
	boolean isUsernameAvailable(@Param("username") String username);

	/**
	 * Checks if a customer with the given username or email exists.
	 *
	 * @param username the username to check
	 * @param email    the email to check
	 * @return true if a customer with the username or email exists, false otherwise
	 */
	boolean existsByUsernameOrEmail(String username, String email);

	/**
	 * Finds a customer by username.
	 *
	 * @param username the username to search for
	 * @return an Optional containing the customer if found, or empty if not found
	 */
	Optional<Customer> findByUsername(String username);

	/**
	 * Finds a customer by email address.
	 *
	 * @param email the email to search for
	 * @return an Optional containing the customer if found, or empty if not found
	 */
	Optional<Customer> findByEmail(String email);


	/**
	 * Retrieves buddies for a given customer username.
	 *
	 * @param username the username of the customer
	 * @return a Set of Customer representing customer's buddies
	 */
	@Query("SELECT c.buddies FROM Customer c WHERE c.username = :username")
	Set<Customer> findBuddiesByCustomerUsername(@Param("username") String username);

}
