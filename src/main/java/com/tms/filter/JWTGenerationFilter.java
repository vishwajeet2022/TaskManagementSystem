package com.tms.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tms.apputil.TMSConstants;
import com.tms.config.JWTUtility;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTGenerationFilter extends OncePerRequestFilter {

	private JWTUtility jwtUtility;

	public JWTGenerationFilter(JWTUtility jwtUtility) {
		super();
		this.jwtUtility = jwtUtility;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
		String jwt = null;
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			jwt = jwtUtility.doGenerateToken(authentication.getName(), authentication.getAuthorities());
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		response.addHeader(TMSConstants.AUTHORIZATION_HEADER, TMSConstants.BEARER + jwt);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return !request.getServletPath().equals("/api/auth/login");
	}
}
