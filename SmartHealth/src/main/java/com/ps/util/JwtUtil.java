package com.ps.util;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ps.config.props.JwtProperties;
import com.ps.constants.ICommonConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	@Autowired
	private JwtProperties jwtProperties;
	
	public String generateToken(String email, Set<String> roles) { 
		return Jwts.builder()
				.setSubject(email)
				.claim(ICommonConstants.ROLE, roles)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(jwtProperties.getExpirationTimeInMinutes())))
				.signWith(Keys.hmacShaKeyFor(jwtProperties.getSecreteKey().getBytes()), SignatureAlgorithm.HS512)
				.compact();
	}
	
	public Claims getClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(jwtProperties.getSecreteKey().getBytes())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	public String getSubject(String token) {
		return getClaims(token).getSubject();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getRoles(String token) {
		return getClaims(token).get(ICommonConstants.ROLE, List.class);
	}
	
	public boolean isTokenValid(String token) {
		try {
			getClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String getEmailFromToken() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
	
	public static String getPrimaryRoleFromToken() {
		SimpleGrantedAuthority role = (SimpleGrantedAuthority) SecurityContextHolder.getContext().getAuthentication().getAuthorities().toArray()[0];
		return role.getAuthority();
	}
}
