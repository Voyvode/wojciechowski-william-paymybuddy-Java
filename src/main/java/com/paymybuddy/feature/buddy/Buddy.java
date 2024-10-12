package com.paymybuddy.feature.buddy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import java.io.Serializable;

@Entity
public class Buddy {

	@EmbeddedId
	private BuddyId id;

}

@Embeddable
class BuddyId implements Serializable {

	@Column(nullable = false)
	private Long customerId;

	@Column(nullable = false)
	private Long buddyId;

}