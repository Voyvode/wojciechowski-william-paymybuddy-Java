package com.paymybuddy.core.config;

import com.paymybuddy.core.security.SecurityUtils;
import com.paymybuddy.feature.customer.Customer;
import com.paymybuddy.feature.customer.CustomerRepository;
import com.paymybuddy.feature.transfer.Transfer;
import com.paymybuddy.feature.transfer.TransferRepository;
import org.h2.tools.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Configuration class for setting up a development database.
 */
@Configuration
@Profile("local")
public class DevDatabaseConfig {

	/**
	 * Starts an internal H2 server to allow querying from the IDE.
	 *
	 * @return the H2 server instance
	 * @throws SQLException if an SQL error occurs while starting the server
	 */
	@Bean(initMethod = "start", destroyMethod = "stop")
	protected Server h2Server() throws SQLException {
		return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
	}

	/**
	 * Initializes the development database with sample customers and transfers.
	 *
	 * @param customerRepo the repository for managing customers
	 * @param transferRepo the repository for managing transfers
	 * @return a CommandLineRunner that populates the database with sample data
	 */
	@Bean
	protected CommandLineRunner initDevDatabase(CustomerRepository customerRepo, TransferRepository transferRepo) {
		return args -> {
			var will = new Customer("will", "w.woj@yandex.com", SecurityUtils.hashPassword("will"));
			var popo = new Customer("pauline", "popo@gmail.com", SecurityUtils.hashPassword("pauline"));
			var gomar = new Customer("margaux", "margal@gmail.com", SecurityUtils.hashPassword("margaux"));
			var tony = new Customer("tony", "mr_tonton@hotmail.fr", SecurityUtils.hashPassword("tony"));
			var sam = new Customer("sam", "johnsnake@msn.com", SecurityUtils.hashPassword("sam"));
			var jojo = new Customer("jordan", "barbar@gmail.com", SecurityUtils.hashPassword("jordan"));

			will.addBuddy(popo);
			will.addBuddy(gomar);
			will.addBuddy(tony);
			will.addBuddy(sam);
			popo.addBuddy(tony);
			popo.addBuddy(gomar);
			gomar.addBuddy(sam);
			gomar.addBuddy(jojo);
			sam.addBuddy(jojo);
			sam.addBuddy(will);
			jojo.addBuddy(will);
			jojo.addBuddy(popo);

			customerRepo.saveAll(Arrays.asList(will, popo, gomar, tony, sam, jojo));

			transferRepo.saveAll(Arrays.asList(
					new Transfer(will, popo, BigDecimal.valueOf(50.00), "Déjeuner"),
					new Transfer(gomar, sam, BigDecimal.valueOf(30.50), "Ciné"),
					new Transfer(tony, popo, BigDecimal.valueOf(15.75), "Café"),
					new Transfer(jojo, will, BigDecimal.valueOf(100.00), "Cadeau d’anniversaire"),
					new Transfer(sam, jojo, BigDecimal.valueOf(75.25), "Courses"),
					new Transfer(will, gomar, BigDecimal.valueOf(20.00), "Petit cadeau"),
					new Transfer(popo, tony, BigDecimal.valueOf(40.00), "Diner"),
					new Transfer(gomar, jojo, BigDecimal.valueOf(60.00), "Transport"),
					new Transfer(sam, will, BigDecimal.valueOf(10.00), "Aide financière"),
					new Transfer(tony, gomar, BigDecimal.valueOf(25.50), "Prêt"),
					new Transfer(popo, sam, BigDecimal.valueOf(85.00), "Fête"),
					new Transfer(will, tony, BigDecimal.valueOf(45.00), "Cadeau de Noël"),
					new Transfer(gomar, will, BigDecimal.valueOf(55.00), "Remboursement"),
					new Transfer(sam, popo, BigDecimal.valueOf(90.00), "Loyer partagé"),
					new Transfer(jojo, tony, BigDecimal.valueOf(35.00), "Sortie au cinéma"),
					new Transfer(popo, gomar, BigDecimal.valueOf(70.00), "Dépenses communes"),
					new Transfer(will, sam, BigDecimal.valueOf(80.00), "Achat groupé"),
					new Transfer(tony, jojo, BigDecimal.valueOf(22.50), "Remboursement de prêt"),
					new Transfer(gomar, tony, BigDecimal.valueOf(12.75), "Dépense imprévue"),
					new Transfer(sam, will, BigDecimal.valueOf(100.00), "Aide pour les courses"),
					new Transfer(popo, jojo, BigDecimal.valueOf(65.00), "Dépense de vacances")
			));
		};
	}

}