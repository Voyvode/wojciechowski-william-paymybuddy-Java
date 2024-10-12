package com.paymybuddy.model;

import javax.money.Monetary;
import java.time.Instant;

public class Transfer {
	Long id;
	Customer sender;
	Customer receiver;
	String description;
	Monetary amount;
	Instant timestamp;
}
