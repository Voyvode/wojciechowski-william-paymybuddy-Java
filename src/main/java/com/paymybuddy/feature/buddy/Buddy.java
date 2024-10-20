package com.paymybuddy.feature.buddy;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "buddy")
@NoArgsConstructor
@Data
public class Buddy {

	@EmbeddedId
	private BuddyId id;

}

@Data
@Embeddable
class BuddyId implements Serializable {

	@Column(nullable = false)
	private Long customerId;

	@Column(nullable = false)
	private Long buddyId;

}