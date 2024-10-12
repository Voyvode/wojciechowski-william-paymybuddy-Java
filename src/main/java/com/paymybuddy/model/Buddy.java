package com.paymybuddy.model;

import lombok.Data;

@Data
public class Buddy {
	Long id;
	String customerId;
	String buddy;
}
//todo: class + annotation JPA