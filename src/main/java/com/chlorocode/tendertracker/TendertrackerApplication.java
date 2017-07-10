package com.chlorocode.tendertracker;

import com.chlorocode.tendertracker.filter.AdminFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
public class TendertrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TendertrackerApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean adminFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new AdminFilter());
		registration.addUrlPatterns("/admin/*");
		return registration;
	}
}
