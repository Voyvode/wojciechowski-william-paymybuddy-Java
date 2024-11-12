package com.paymybuddy.core.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Optional;

/**
 * Security service for password hashing and verification.
 *
 * <p>Uses PBKDF2 with HMAC-SHA256 and random salting for security.
 */
public final class SecurityUtils {

	private static final int ITERATIONS = 65536;
	private static final int KEY_LENGTH = 256;
	private static final int SALT_LENGTH = 8;

	private static final SecureRandom secureRandom = new SecureRandom();
	private static final SecretKeyFactory secretKeyFactory;

	static {
		try {
			secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Cryptographic algorithm not available", e);
		}
	}

	/**
	 * Hashes a password with random salt.
	 *
	 * @param password the plain text password.
	 * @return the salt and hash, Base64-encoded and separated by ":".
	 */
	public static String hashPassword(String password) {
		var salt = new byte[SALT_LENGTH];
		secureRandom.nextBytes(salt);

		return hashPassword(salt, password);
	}

	/**
	 * Hashes a password with given salt.
	 *
	 * @param salt     the salt byte array.
	 * @param password the plain text password.
	 * @return Base64-encoded salt and hash, separated by ":".
	 * @throws IllegalArgumentException if the key spec is invalid.
	 */
	private static String hashPassword(byte[] salt, String password) {
		var keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
		try {
			byte[] hash = secretKeyFactory.generateSecret(keySpec).getEncoded();
			return "%s:%s".formatted(Base64.getEncoder().encodeToString(salt), Base64.getEncoder().encodeToString(hash));
		} catch (InvalidKeySpecException e) {
			throw new IllegalArgumentException("Invalid key specification", e);
		}
	}

	/**
	 * Verifies a plain password against a hashed password.
	 *
	 * @param plainTextPassword the plain text password.
	 * @param hashedPassword    the hashed password with salt.
	 * @return {@code true} if passwords match, {@code false} otherwise.
	 */
	public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
		var salt = Optional.of(hashedPassword.split(":"))
				.filter(parts -> parts.length == 2)
				.map(parts -> Base64.getDecoder().decode(parts[0]))
				.orElseThrow(() -> new IllegalArgumentException("Invalid hashed password format"));

		var testedPassword = hashPassword(salt, plainTextPassword);
		return testedPassword.equals(hashedPassword);
	}

	/**
	 * Validates the strength of a password.
	 *
	 * @param password the password to validate.
	 * @return {@code true} if the password meets the strength criteria, {@code false} otherwise.
	 */
	public static boolean isPasswordStrong(String password) {
		return password != null
				&& password.length() >= 12
				&& password.matches(".*[A-Z].*")
				&& password.matches(".*[a-z].*")
				&& password.matches(".*\\d.*")
				&& password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
	}

}
