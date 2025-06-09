package com.tms.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.tms.filter.JWTGenerationFilter;
import com.tms.filter.JWTValidationFilter;
import static //
org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
public class SecurityConfig {

	@Autowired
	JWTUtility jwtUtility;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.securityContext(contextConfig -> contextConfig.requireExplicitSave(false))
				.sessionManagement(
						sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable()).cors(cors -> cors.disable())
				.addFilterBefore(new JWTValidationFilter(jwtUtility), UsernamePasswordAuthenticationFilter.class)
				.addFilterAfter(new JWTGenerationFilter(jwtUtility), BasicAuthenticationFilter.class)
				.authorizeHttpRequests(request -> request.requestMatchers("/users/**", "/tasks/**")
						.hasAnyRole("USER", "ADMIN").requestMatchers("/api/auth/register").permitAll()
						.requestMatchers(toH2Console()).permitAll().anyRequest().authenticated())
				.httpBasic(withDefaults()).formLogin(withDefaults())
				.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));
		return httpSecurity.build();

	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		CustomUserNamePasswordAuthenticationProvider authenticationProvider = new CustomUserNamePasswordAuthenticationProvider(
				userDetailsService, passwordEncoder);
		ProviderManager providerManager = new ProviderManager(authenticationProvider);
		providerManager.setEraseCredentialsAfterAuthentication(false);
		return providerManager;
	}

}
