package com.paymybuddy.feature.buddy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuddyRepository extends JpaRepository<Buddy, Long> {

	List<Buddy> findById_CustomerId(Long customerId);
	boolean existsById(BuddyId id);

}
