package com.paymybuddy.core.config;

import com.paymybuddy.core.security.SecurityService;
import com.paymybuddy.feature.customer.Customer;
import com.paymybuddy.feature.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DummyDataConfig {

	@Bean
	CommandLineRunner initDatabase(CustomerRepository customerRepository, SecurityService securityService) {
		return args -> {
			customerRepository.save(new Customer("will", "w.woj@yandex.com", securityService.hashPassword("will")));
			customerRepository.save(new Customer("pauline", "popo@gmail.com", securityService.hashPassword("pauline")));
			customerRepository.save(new Customer("margaux", "margal@gmail.com", securityService.hashPassword("margaux")));
			customerRepository.save(new Customer("tony", "mr_tonton@hotmail.fr", securityService.hashPassword("tony")));
			customerRepository.save(new Customer("sam", "johnsnake@msn.com", securityService.hashPassword("sam")));
			customerRepository.save(new Customer("jordan", "barbar@gmail.com", securityService.hashPassword("jordan")));

			// TODO: Ajouter Buddy list
			// TODO: Ajouter transactions
		};
	}

}