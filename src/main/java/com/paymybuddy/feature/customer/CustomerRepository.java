package com.paymybuddy.feature.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByUsername(String username);
	Optional<Customer> findByEmail(String email);
	boolean existsByUsername(String username);
	boolean existsByUsernameNot(String username);
	boolean existsByEmail(String email);
	boolean existsByEmailNot(String email);

}
