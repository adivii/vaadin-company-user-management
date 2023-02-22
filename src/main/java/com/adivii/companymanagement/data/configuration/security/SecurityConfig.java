package com.adivii.companymanagement.data.configuration.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.adivii.companymanagement.data.entity.User;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // Define constant link for login and logout
    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/login";

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                // Client-side JS
                "/VAADIN/**",

                // the standard favicon URI
                "/favicon.ico",

                // the robots exclusion standard
                "/robots.txt",

                // web application manifest
                "/manifest.webmanifest",
                "/sw.js",
                "/offline.html",

                // icons and images
                "/icons/**",
                "/images/**",
                "/styles/**",

                // (development mode) H2 debugging console
                "/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Handles CSRF Manually
        http.csrf().disable()
                // Register CustomRequestCache to save unauthorized request
                .requestCache().requestCache(new CustomRequestCache())
                // Restrict access
                .and().authorizeRequests()
                // Allow Vaadin's internal request
                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
                // Allow request by logged-in users
                .antMatchers("/company").hasAuthority("superadmin")
                .antMatchers("/department").hasAuthority("departmentadmin")
                .anyRequest().authenticated()
                // Configure login page
                .and().formLogin()
                .loginPage(LOGIN_URL).permitAll()
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                .failureUrl(LOGIN_FAILURE_URL)
                // Configure Logout
                .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
}
