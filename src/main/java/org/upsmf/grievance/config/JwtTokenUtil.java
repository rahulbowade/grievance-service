package org.upsmf.grievance.config;

import static org.upsmf.grievance.util.Constants.ACCESS_TOKEN_VALIDITY_SECONDS;
import static org.upsmf.grievance.util.Constants.JWT_GRANTED_AUTHORITY;
import static org.upsmf.grievance.util.Constants.JWT_ISSUER;
import static org.upsmf.grievance.util.Constants.SIGNING_KEY;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.upsmf.grievance.dto.UserDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final String CLAIMS_KEY = "scopes";
	public static final String USER_REF = "userReference";
	public static final String ORG_REF = "orgReference";

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Long getUserIdFromToken(String token) {
		final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		MyCustomJwtClaims customClaims = null;
		try {
			customClaims = mapper.convertValue(getAllClaimsFromToken(token), MyCustomJwtClaims.class);
		} catch (Exception e) {
			e.getMessage();
		}
		if (customClaims != null && customClaims.getUserReference() != null) {
			return customClaims.getUserReference();
		}
		return null;
	}

	public Long getOrgIdFromToken(String token) {
		final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		MyCustomJwtClaims customClaims = null;
		try {
			customClaims = mapper.convertValue(getAllClaimsFromToken(token), MyCustomJwtClaims.class);
		} catch (Exception e) {
			e.getMessage();
		}
		if (customClaims != null && customClaims.getOrgReference() != null) {
			return customClaims.getOrgReference();
		}
		return null;
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String generateToken(UserDto user) {
		return doGenerateToken(user.getUsername(), user.getId(), user.getOrgId());
	}

	private String doGenerateToken(String subject, Long userId, Long orgId) {

		Claims claims = Jwts.claims().setSubject(subject);
		claims.put(CLAIMS_KEY, Arrays.asList(new SimpleGrantedAuthority(JWT_GRANTED_AUTHORITY)));
		claims.put(USER_REF, userId);
		claims.put(ORG_REF, orgId);

		return Jwts.builder().setClaims(claims).setIssuer(JWT_ISSUER).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
				.signWith(SignatureAlgorithm.HS256, SIGNING_KEY).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

}
