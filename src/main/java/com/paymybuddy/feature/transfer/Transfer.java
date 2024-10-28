package com.paymybuddy.feature.transfer;

import com.paymybuddy.feature.customer.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transfer")
@NoArgsConstructor
@Data
public class Transfer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(nullable = false, name = "sender_id")
	private Customer sender;

	@ManyToOne
	@JoinColumn(nullable = false, name = "receiver_id")
	private Customer receiver;

	@Column
	private String description;

	@Column(precision = 4, scale = 2)
	@DecimalMin(value = "0.01", message = "amount must be strictly greater than 0")
	@DecimalMax(value = "1000.00", message = "amount must not exceed 1,000")
	private BigDecimal amount;

	@Column(nullable = false)
	Instant timestamp;

}
