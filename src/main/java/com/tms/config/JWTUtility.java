package com.tms.config;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.tms.model.Authority;
import com.tms.model.Authority.AuthorityCode;
import com.tms.model.TMSUser.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JWTUtility implements Serializable {

	private static final long serialVersionUID = 234234523523L;

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;

	private SecretKey secretKey;

	@PostConstruct
	protected void init() {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public String getCommaSeparatedAuthoritesFromToken(String token) {
		String authoritySting = (String) getAllClaimsFromToken(token).get("authorities");
		String authorities = "";
		for (AuthorityCode authCode : Authority.AuthorityCode.values()) {
			if (authoritySting.contains(authCode.toString())) {
				authorities = authorities + authCode.toString() + ",";
			}
		}
		for (Role role : Role.values()) {
			if (authoritySting.contains(role.toString())) {
				authorities = authorities + role.toString() + ",";
			}
		}

		if (StringUtils.hasText(authorities))
			authorities = authorities.substring(0, authorities.length() - 1);
		return authorities;
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// for retrieving any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String doGenerateToken(String userName, Collection<? extends GrantedAuthority> authorties) {
		return Jwts.builder().issuer("tms.com").subject(userName)
				.claim("authorities", this.populateAuthoritiesInJWT(authorties)).issuedAt(new Date())
				.expiration(new Date(new Date().getTime() + expiration)).signWith(secretKey).compact();
	}

	private String populateAuthoritiesInJWT(Collection<? extends GrantedAuthority> authorties) {

		String auth = "";
		for (GrantedAuthority authority : authorties) {
			auth = auth + authority.getAuthority();
		}
		return String.join(",", auth);
	}

	// validate token
	public Boolean validateToken(String token, String username) {

		final String usernamefromToken = getUsernameFromToken(token);
		return (username.equals(usernamefromToken) && !isTokenExpired(token));
	}
}