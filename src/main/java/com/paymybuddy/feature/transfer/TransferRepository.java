package com.paymybuddy.feature.transfer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

//	List<Transfer> findAllBySender(Long senderId);

}
