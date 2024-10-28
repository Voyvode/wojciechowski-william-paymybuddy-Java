package com.paymybuddy.feature.transfer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

	@Query("SELECT t FROM Transfer t WHERE t.sender.username = :customerUsername ORDER BY t.timestamp DESC")
	List<Transfer> findAllTransfersForCustomer(String customerUsername);

}
