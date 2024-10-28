package com.paymybuddy.feature.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDTO {

	@NotBlank @Email
	private String email;
	private String emailConfirm;

	@NotBlank
	private String username;

	@NotBlank
	private String password;
	private String passwordConfirm;

	public boolean isEmailMatching() {
		return email.equals(emailConfirm);
	}

	public boolean isPasswordMatching() {
		return password.equals(passwordConfirm);
	}

}