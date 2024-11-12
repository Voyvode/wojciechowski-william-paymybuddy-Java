package com.paymybuddy.feature.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for Customer information.
 *
 * <p>Used for registration and profile update operations.
 */
@Data
@Builder
public class CustomerDTO {

	@Email
	@NotNull
	private String email;

	private String emailConfirm;

	@NotBlank
	private String username;

	@NotBlank
	@Size(min = 12)
	private String password;

	private String passwordConfirm;

	/**
	 * Checks if email matches confirmation.
	 *
	 * @return true if email and emailConfirm are equal, false otherwise
	 */
	public boolean isEmailMatching() {
		return email != null && email.equals(emailConfirm);
	}

	/**
	 * Checks if password matches confirmation.
	 *
	 * @return true if password and passwordConfirm are equal, false otherwise
	 */
	public boolean isPasswordMatching() {
		return password != null && password.equals(passwordConfirm);
	}

}
