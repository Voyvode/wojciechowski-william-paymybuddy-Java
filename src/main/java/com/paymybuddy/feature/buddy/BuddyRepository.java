package com.paymybuddy.feature.buddy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface BuddyRepository extends JpaRepository<Buddy, Long> {

	boolean existsById(BuddyId id);

}
