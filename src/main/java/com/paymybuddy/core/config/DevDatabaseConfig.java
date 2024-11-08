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

@Configuration
@Profile("dev")
public class DevDatabaseConfig {

	// Start an internal H2 server to query from IDE
	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server h2Server() throws SQLException {
		return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
	}

	@Bean
	CommandLineRunner initDevDatabase(CustomerRepository customerRepository, TransferRepository transferRepository) {
		return args -> {
			var will = new Customer("will", "w.woj@yandex.com", SecurityUtils.hashPassword("will"));
			var popo = new Customer("pauline", "popo@gmail.com", SecurityUtils.hashPassword("pauline"));
			var gomar = new Customer("margaux", "margal@gmail.com", SecurityUtils.hashPassword("margaux"));
			var tony = new Customer("tony", "mr_tonton@hotmail.fr", SecurityUtils.hashPassword("tony"));
			var sam = new Customer("sam", "johnsnake@msn.com", SecurityUtils.hashPassword("sam"));
			var jojo = new Customer("jordan", "barbar@gmail.com", SecurityUtils.hashPassword("jordan"));

			will.addBuddy(popo);
			will.addBuddy(gomar);
			popo.addBuddy(tony);
			gomar.addBuddy(sam);
			sam.addBuddy(jojo);
			jojo.addBuddy(will);

			customerRepository.saveAll(Arrays.asList(will, popo, gomar, tony, sam, jojo));

			var t1 = new Transfer(will, popo, BigDecimal.valueOf(50.00), "Déjeuner");
			var t2 = new Transfer(gomar, sam, BigDecimal.valueOf(30.50), "Ciné");
			var t3 = new Transfer(tony, popo, BigDecimal.valueOf(15.75), "Café");
			var t4 = new Transfer(jojo, will, BigDecimal.valueOf(100.00), "Cadeau d’anniversaire");
			var t5 = new Transfer(sam, jojo, BigDecimal.valueOf(75.25), "Courses");

			transferRepository.saveAll(Arrays.asList(t1, t2, t3, t4, t5));
		};
	}

}