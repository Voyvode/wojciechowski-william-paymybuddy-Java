package com.paymybuddy.feature.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByUsername(String username);
	boolean existsByUsernameOrEmail(String username, String email);

	Optional<Customer> findByUsername(String username);
	Optional<Customer> findByEmail(String email);

	@Query("SELECT c.buddies FROM Customer c WHERE c.username = :customerUsername")
	Set<Customer> findBuddiesByCustomerUsername(@Param("customerUsername") String username);

}
