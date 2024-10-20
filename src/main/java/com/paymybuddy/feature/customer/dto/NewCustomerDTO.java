package com.paymybuddy.feature.customer.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NonNull;

@Data
public class NewCustomerDTO {

	@NonNull
	private String username;

	@NonNull @Email
	private String email;
	private String emailConfirm;

	@NonNull
	private String password;
	private String passwordConfirm;

	public boolean isEmailMatching() {
		return email.equals(emailConfirm);
	}

	public boolean isPasswordMatching() {
		return password.equals(passwordConfirm);
	}

}

