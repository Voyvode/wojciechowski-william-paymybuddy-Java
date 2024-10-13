package com.paymybuddy.core.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

	private final BCryptPasswordEncoder passwordEncoder;

	public SecurityService() {
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

	public String hashPassword(String plainTextPassword) {
		return passwordEncoder.encode(plainTextPassword);
	}

	public boolean verifyPassword(String plainTextPassword, String hashedPassword) {
		return passwordEncoder.matches(plainTextPassword, hashedPassword);
	}

}
