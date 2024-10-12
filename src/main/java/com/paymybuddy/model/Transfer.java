package com.paymybuddy.model;

import lombok.Data;

import javax.money.Monetary;
import java.time.Instant;

@Data
public class Transfer {
	Long id;
	Customer sender;
	Customer receiver;
	String description;
	Monetary amount;
	Instant timestamp;
}
