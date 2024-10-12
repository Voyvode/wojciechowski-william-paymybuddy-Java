package com.paymybuddy.model;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class Customer {
	Long id;
	String username;
	String email;
	String passwordHash;
	List<Customer> buddies;
	Instant signup;
}
