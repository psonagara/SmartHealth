package com.ps.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IPatientConstants;
import com.ps.filter.SecurityFilter;

/**
 * Configuration class for Spring Security settings in the application.
 * Configures a stateless JWT-based authentication with role-based access control,
 * disables CSRF and form login, and integrates a custom SecurityFilter.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private SecurityFilter securityFilter;
	
	/**
     * Configures the SecurityFilterChain to define authentication and authorization rules.
     * - Disables CSRF and form login for stateless API.
     - Permits all requests to auth, data, Swagger, and v3 endpoints.
     - Restricts patient, doctor, and admin endpoints to respective roles.
     - Enforces stateless session management with JWT.
     - Adds the custom SecurityFilter before UsernamePasswordAuthenticationFilter.
     *
     * @param httpSecurity the HttpSecurity instance to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
		.csrf(csrf -> csrf.disable())
		.cors(Customizer.withDefaults())
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/auth/**", "/data/**", "/v3/**", "/swagger-ui/**").permitAll()
				.requestMatchers("/appointment/change/status/**", "/appointment/view/all/**").hasAnyAuthority(IDoctorConstants.DOCTOR_ROLE, IPatientConstants.PATIENT_ROLE)
				.requestMatchers("/patient/**", "/appointment/**").hasAuthority(IPatientConstants.PATIENT_ROLE)
				.requestMatchers("/availability/**").hasAuthority(IDoctorConstants.DOCTOR_ROLE)
				.requestMatchers("/admin/**").hasAuthority(IAdminConstants.ADMIN_ROLE)
				.anyRequest().authenticated()
				)
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.httpBasic(httpBasic -> httpBasic.disable())
		.formLogin(formLogin -> formLogin.disable())
		.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
		return httpSecurity.build();
	}
	
	/**
     * Provides the AuthenticationManager bean for authentication handling.
     *
     * @param config the AuthenticationConfiguration to retrieve the manager
     * @return the configured AuthenticationManager
     * @throws Exception if configuration fails
     */
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	    return config.getAuthenticationManager();
	}
}
