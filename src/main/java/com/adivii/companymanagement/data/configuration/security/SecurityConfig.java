package com.adivii.companymanagement.data.configuration.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.adivii.companymanagement.data.service.ErrorService;
import com.adivii.companymanagement.data.service.security.CustomPasswordEncoder;

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
        return new CustomPasswordEncoder();
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
                .antMatchers("/company").hasAnyAuthority("superadmin", "companyadmin")
                .antMatchers("/department").hasAnyAuthority("superadmin", "departmentadmin")
                .antMatchers("/user").hasAnyAuthority("superadmin", "useradmin")
                .anyRequest().authenticated()
                // Configure login page
                .and().formLogin()
                .loginPage(LOGIN_URL).permitAll()
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                .failureUrl(LOGIN_FAILURE_URL)
                .successHandler(new AuthenticationSuccessHandler() {

                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                            Authentication authentication) throws IOException, ServletException {
                        // TODO: Search for different between HttpSession and VaadinSession
                        HttpSession session = request.getSession();
                        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                        ErrorService errorService = new ErrorService(false, null);

                            if(!userDetails.getUser().isActivated()) {
                                errorService.setErrorStatus(true);
                                errorService.setErrorMessage("Your Password seems to use the default template password, please change it immediately");
                            }
                        
                        session.setAttribute("userID", userDetails.getUser().getUserId());
                        session.setAttribute("errorStatus", errorService);
                        session.setMaxInactiveInterval(1800); // Inactive Interval in Second(s)

                        response.sendRedirect("/");
                    }

                })
                // Configure Logout
                .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL).addLogoutHandler(new LogoutHandler() {

                    @Override
                    public void logout(HttpServletRequest request, HttpServletResponse response,
                            Authentication authentication) {
                        HttpSession session = request.getSession();
                        session.invalidate();
                    }
                })
                // Configure 403 Page
                .and().exceptionHandling().accessDeniedPage("/dashboard");
    }

    /*~~(Migrate manually based on https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)~~>*/@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
}
