package com.paymybuddy.feature.transfer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Transfer entities.
 *
 * <p>Provides methods for querying transfer data related to customers.
 */
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

	/**
	 * Retrieves all transfers made by a specific customer, ordered by timestamp in descending order.
	 *
	 * @param customerUsername the username of the customer whose transfers are to be retrieved
	 * @return a list of transfers made by the specified customer
	 */
	@Query("SELECT t FROM Transfer t WHERE t.sender.username = :customerUsername ORDER BY t.timestamp DESC")
	List<Transfer> findAllTransfersForCustomer(String customerUsername);

}
