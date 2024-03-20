package com.inventory.app.server.security;

import com.inventory.app.server.service.authorization.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This custom 'JwtAuthFilter' overrides the 'doFilterInternal' method which is invoked for every request to our application. This filter intercepts requests, looks for Bearer tokens,
 * validates them, and authenticates users if the token is valid. Requests without a valid BEarer token continue through the filter chain, while authenticated users gain access to
 * protected resources.
 */
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailServiceImpl;

    /**
     * This method is responsible for processing incoming requests by inspecting the "Authorization" header to identify and validate a Bearer token.
     *
     * 1.) The filter checks for an "Authorization" header in the request, and if it contains a Bearer token. Requests unrelated to user login typically do not  have
     * a JWT token in their headers, so they pass through to the next filter chain without any token-related processing.
     *
     * 2.) If the "Authorization" header is found, and it starts with "Bearer", which indicates the presence of an access token, the filter will then validate and authenticate
     * this token.
     *
     * 3.) The validation process starts by extracting the access token from the header.
     *
     * 4.) The JwtService validates the token, ensuring it's a valid and unexpired token.
     *
     * 5.) If the token is valid, the filter authenticates the request by creating an Authentication object. The object represents the user's
     * authentication status and contains details about the user.
     *
     * 6.) The authenticated user is then stored in the 'Security Context', ensuring they have access to protected resources in the application.
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
