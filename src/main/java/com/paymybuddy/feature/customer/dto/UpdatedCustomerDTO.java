package com.paymybuddy.feature.customer.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdatedCustomerDTO {

	private String username;

	@Email
	private String email;

	private String password;

}
