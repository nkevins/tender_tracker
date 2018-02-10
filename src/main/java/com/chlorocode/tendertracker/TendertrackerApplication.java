package com.chlorocode.tendertracker;

import com.chlorocode.tendertracker.filter.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.TimeZone;
import java.util.concurrent.Executor;

/**
 * The base class of the application. Application will be started from this class.
 */
@SpringBootApplication
@EnableAsync
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
@EnableScheduling
public class TendertrackerApplication {

	@Value("${application.timezone}")
	private String timeZone;

	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	public static void main(String[] args) {
		SpringApplication.run(TendertrackerApplication.class, args);
	}

	/**
	 * This method used to create the admin filter.
	 *
	 * @return FilterRegistrationBean
	 */
	@Bean
	public FilterRegistrationBean adminFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new AuthorizationFilter());
		registration.addUrlPatterns("*");
		return registration;
	}

	/**
	 * This method used to create the executor for async threads.
	 *
	 * @return Executor
	 */
	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("GithubLookup-");
		executor.initialize();
		return executor;
	}

}
