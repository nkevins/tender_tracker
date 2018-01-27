package com.chlorocode.tendertracker;

import com.chlorocode.tendertracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * This class is used to control the security of the application by using spring security.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private AuthService authService;

    /**
     * Constructor.
     *
     * @param authService AuthService
     */
    @Autowired
    public WebSecurityConfig(AuthService authService) {
        this.authService = authService;
    }

    /**
     * This method is used to configure the HttpSecurity control of the application.
     *
     * @param http HttpSecurity
     * @throws Exception Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/registerCompany").authenticated()
                    .antMatchers("/tenderNotification").authenticated()
                    .antMatchers("/user/profile").authenticated()
                    .antMatchers("/admin").access("hasAnyRole('ADMIN','SYS_ADMIN','PREPARER','SUBMITTER')")
                    .antMatchers("/admin/**").access("hasAnyRole('ADMIN','PREPARER','SUBMITTER')")
                    .antMatchers("/sysadm/**").access("hasRole('SYS_ADMIN')")
                    .antMatchers("/**").permitAll()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .failureUrl("/login?error")
                    .usernameParameter("email")
                    .defaultSuccessUrl("/")
                    .failureHandler(authenticationFailureHandler)
                    .permitAll()
                    .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .permitAll()
                    .and()
                .csrf().ignoringAntMatchers("/restapi/**");
    }

    /**
     * This method is used to configure the WebSecurity control of the application.
     *
     * @param web WebSecurity
     * @throws Exception exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/data/**");
    }

    /**
     * This method is used to configure the AuthenticationManagerBuilder for login of the application.
     *
     * @param auth AuthenticationManagerBuilder
     * @throws Exception exception
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(authService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }
}
