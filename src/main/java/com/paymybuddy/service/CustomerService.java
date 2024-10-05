package com.paymybuddy.service;

import com.paymybuddy.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

	private JdbcTemplate jdbcTemplate;

	public List<Customer> getAllCustomers() {
		String sql = "SELECT id, username, email FROM customer";
		return jdbcTemplate.query(sql, (rs, rowNum) ->
				new Customer(
						rs.getInt("id"),
						rs.getString("username"),
						rs.getString("email"),
						rs.getString("password_hash"),
						new ArrayList<>() // Vous devrez charger les connections séparément
				)
		);
	}

	public void addCustomer() {
//		String sql = "INSERT INTO customer ('scrooge', 'scrooge@mcduck.com', 'greedisgood') VALUES (?, ?, ?)";
//		jdbcTemplate.update(sql, userDTO.username(), userDTO.email(), userDTO.password());
	}

	public void updateCustomer() {

	}

	// Ajoutez d'autres méthodes pour les transactions, connections, etc.
}