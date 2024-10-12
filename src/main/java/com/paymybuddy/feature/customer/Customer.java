package com.paymybuddy.feature.customer;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 32)
	private String username;

	@Column(nullable = false, unique = true, length = 254)
	private String email;

	@Column(nullable = false, length = 60)
	private String password_hash;

//	List<Customer> buddies;

	@Column(nullable = false)
	private Instant signup;
}
