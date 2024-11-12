package com.paymybuddy.feature.customer;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a customer in the PayMyBuddy application.
 *
 * <p>Stores essential information about users and their relationships with other customers.
 */
@Entity
@Table(name = "customer")
@NoArgsConstructor
@Data
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false, unique = true, length = 32)
	private String username;

	@NotBlank
	@Email
	@Column(nullable = false, unique = true, length = 254)
	private String email;

	@NotBlank
	@Column(nullable = false, length = 60)
	private String passwordHash;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Column
	private Instant lastLoginAt;

	@ManyToMany
	@EqualsAndHashCode.Exclude
	@JoinTable(
			name = "customer_buddy",
			joinColumns = @JoinColumn(name = "customer_id"),
			inverseJoinColumns = @JoinColumn(name = "buddy_id")
	)
	private Set<Customer> buddies = new HashSet<>();

	/**
	 * Creates a new customer with the given details.
	 *
	 * @param username     the customer's username
	 * @param email        the customer's email address
	 * @param passwordHash the hashed password of the customer
	 */
	public Customer(String username, String email, String passwordHash) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.createdAt = Instant.now();
	}

	/**
	 * Adds another customer to buddy list.
	 *
	 * @param otherCustomer the customer to be added to buddy list
	 */
	public void addBuddy(Customer otherCustomer) {
		if (!email.equals(otherCustomer.getEmail())) {
			buddies.add(otherCustomer);
		}
	}

}
