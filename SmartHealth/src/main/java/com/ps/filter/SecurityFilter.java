package com.ps.filter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ps.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom Spring Security filter that validates JSON Web Tokens (JWT) for each incoming HTTP request.
 * <p>
 * This filter extends {@link OncePerRequestFilter} to guarantee execution only once per request.
 * It extracts the JWT from the {@code Authorization} header, validates it using {@link JwtUtil},
 * and, if valid, sets the authentication context in Spring Security.
 * </p>
 *
 * <p><b>Workflow:</b></p>
 * <ol>
 *   <li>Read the {@code Authorization} header from the HTTP request.</li>
 *   <li>Extract the token if it follows the "Bearer &lt;token&gt;" format.</li>
 *   <li>Validate the token's authenticity and expiration.</li>
 *   <li>Retrieve user email and roles from the token.</li>
 *   <li>Set the {@link UsernamePasswordAuthenticationToken} in the {@link SecurityContextHolder}.</li>
 * </ol>
 *
 * <p>Invalid or missing tokens will not populate the authentication context,
 * but the request will still be passed along the filter chain.</p>
 *
 * @see JwtUtil
 * @see OncePerRequestFilter
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	private static final Logger LOG = LoggerFactory.getLogger(SecurityFilter.class);

	/**
     * Performs JWT token extraction and validation for every HTTP request.
     * <p>
     * If a valid token is found, the method sets the authenticated user's details
     * into the Spring Security context. Otherwise, it leaves the context unauthenticated.
     * </p>
     *
     * @param request     the {@link HttpServletRequest} being processed
     * @param response    the {@link HttpServletResponse} associated with the request
     * @param filterChain the {@link FilterChain} for invoking the next filter
     * @throws ServletException in case of general servlet errors
     * @throws IOException      in case of I/O errors during request processing
     */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = extractToken(request);
		LOG.debug("Entering SecurityFilter.doFilterInternal, Token: {}", token);
		if(token != null) {
			if(jwtUtil.isTokenValid(token)) {
				String email = jwtUtil.getSubject(token);
				List<SimpleGrantedAuthority> roles = jwtUtil.getRoles(token)
						.stream()
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, roles);
				SecurityContextHolder.getContext().setAuthentication(auth);
				LOG.debug("Authentication set for user: {}", email);
			} else {
				LOG.warn("Invalid token detected: {}", token);
			}
		} else {
			LOG.debug("No token provided in request");
		}
		LOG.debug("Exiting SecurityFilter.doFilterInternal");
		filterChain.doFilter(request, response);
	}
	
	/**
     * Extracts the JWT token from the {@code Authorization} header of the HTTP request.
     * <p>
     * The expected format is: {@code Authorization: Bearer <token>}.
     * </p>
     *
     * @param request the {@link HttpServletRequest} from which to extract the token
     * @return the extracted JWT token as a {@link String}, or {@code null} if not found or invalid format
     */
	private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
