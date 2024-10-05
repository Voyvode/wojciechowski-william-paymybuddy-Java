package com.paymybuddy.model;

import javax.money.Monetary;
import java.time.LocalDateTime;

public record Transfer(
		int id,
		LocalDateTime date,
		Customer sender,
		Customer receiver,
		String description,
		Monetary amount
) { }
