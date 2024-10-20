package com.paymybuddy.feature.customer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "customer")
@NoArgsConstructor
@Data
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 32)
	private String username;

	@Column(nullable = false, unique = true, length = 254)
	private String email;

	@Column(nullable = false, length = 60)
	private String passwordHash;

	@Column(nullable = false)
	private Instant signupAt;

	public Customer(String username, String email, String passwordHash) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		signupAt = Instant.now();
	}

}
