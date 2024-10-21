package com.paymybuddy.feature.buddy;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface BuddyRepository extends JpaRepository<Buddy, BuddyId> {

	boolean existsById(@NotNull BuddyId id);

	@Query("SELECT c.username FROM Customer c WHERE c.id IN " +
			"(SELECT b.id.buddyId FROM Buddy b WHERE b.id.customerId = :customerId)")
	List<String> findBuddyUsernamesByCustomerId(@Param("customerId") Long customerId);

}
