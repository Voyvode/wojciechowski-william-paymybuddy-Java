package com.paymybuddy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class SpringJdbcConfig {

	@Value("${spring.datasource.url}")
	private String dataBaseURL;

	@Value("${spring.datasource.username}")
	private String dataBaseUsername;

	@Value("${spring.datasource.password}")
	private String dataBasePassword;

	@Bean
	public DataSource psqlDataSource() {
		var dataSource = new DriverManagerDataSource();
		dataSource.setUrl(dataBaseURL);
		dataSource.setUsername(dataBaseUsername);
		dataSource.setPassword(dataBasePassword);

		return dataSource;
	}

}