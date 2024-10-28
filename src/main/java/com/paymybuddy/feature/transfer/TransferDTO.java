package com.paymybuddy.feature.transfer;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class TransferDTO {

	private Long id;

	@NotNull
	private Long senderId;

	@NotNull
	private Long receiverId;

	@NotNull
	@DecimalMin(value = "0.01")
	@DecimalMax(value = "1000.00")
	private BigDecimal amount;

	private String description;

	private Instant timestamp;

	// Champs supplémentaires pour l'affichage
	private String senderUsername;
	private String receiverUsername;

}
