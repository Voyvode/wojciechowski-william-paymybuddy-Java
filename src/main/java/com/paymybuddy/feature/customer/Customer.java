package com.paymybuddy.feature.customer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 32)
	private String username;

	@Column(nullable = false, unique = true, length = 254)
	private String email;

	@Setter
	@Column(nullable = false, length = 60, name = "password_hash")
	private String passwordHash;

//	List<Customer> buddies;

	@Column(nullable = false)
	private Instant signup;

	public Customer(String username, String email, String passwordHash) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		signup = Instant.now();
	}

}
