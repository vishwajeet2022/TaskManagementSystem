package com.tms.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tms.apputil.TMSConstants;
import com.tms.config.JWTUtility;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTValidationFilter extends OncePerRequestFilter {

	private JWTUtility jwtUtility;

	public JWTValidationFilter(JWTUtility jwtUtility) {
		super();
		this.jwtUtility = jwtUtility;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader(TMSConstants.AUTHORIZATION_HEADER);
		if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
			String jwt = header.substring(7);
			String userName = jwtUtility.getUsernameFromToken(jwt);
			if (jwtUtility.validateToken(jwt, userName)) {

				Authentication authentication = new UsernamePasswordAuthenticationToken(userName, null, AuthorityUtils
						.commaSeparatedStringToAuthorityList(jwtUtility.getCommaSeparatedAuthoritesFromToken(jwt)));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return request.getServletPath().equals("/api/auth/login");
	}
}
