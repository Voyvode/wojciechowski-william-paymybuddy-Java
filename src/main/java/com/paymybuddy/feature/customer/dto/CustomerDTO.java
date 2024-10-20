package com.paymybuddy.feature.customer.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NonNull;

@Data
public class CustomerDTO {

	@NonNull @Email
	private String email;

	@NonNull
	private String password;

}
