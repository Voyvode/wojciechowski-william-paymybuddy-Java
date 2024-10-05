package com.paymybuddy.model;

import java.util.List;

public record Customer(
		int id,
		String username,
		String email,
		String password_hash,
		List<Customer> buddies
) { }
