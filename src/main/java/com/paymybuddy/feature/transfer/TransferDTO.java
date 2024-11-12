package com.paymybuddy.feature.transfer;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for transfers.
 *
 * <p>This DTO is used to encapsulate the data required for a transfer request.
 */
@Data
@Builder
public class TransferDTO {

	@NotNull
	private String receiverUsername;

	@NotNull
	@Positive
	@DecimalMax(value = "1000.00")
	private BigDecimal amount;

	private String description;

}
