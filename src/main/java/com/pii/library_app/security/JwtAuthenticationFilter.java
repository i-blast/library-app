package com.pii.library_app.security;


import com.pii.library_app.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOG.trace("➤➤➤➤➤➤➤ No valid Authorization header found, skipping JWT processing. Request URI: {}", request.getRequestURI());
            chain.doFilter(request, response);
            return;
        }

        var token = authHeader.substring(7);
        LOG.debug("➤➤➤➤➤➤➤ Extracted JWT Token: {}", token);

        String username;
        try {
            username = jwtUtil.extractUsername(token);
            LOG.debug("➤➤➤➤➤➤➤ Extracted Username: {}", username);
        } catch (Exception e) {
            LOG.error("➤➤➤➤➤➤➤ Failed to extract username from token: {}", token, e);
            chain.doFilter(request, response);
            return;
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(username);
            LOG.debug("➤➤➤➤➤➤➤ UserDetails loaded: {}", userDetails.getUsername());
            LOG.debug("User roles: {}", userDetails.getAuthorities());

            if (jwtUtil.validateToken(token)) {
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                LOG.info("➤➤➤➤➤➤➤ User authenticated successfully: {}", userDetails.getUsername());
                LOG.debug("➤➤➤➤➤➤➤ SecurityContext updated with authentication: {}", authentication);
            } else {
                LOG.error("➤➤➤➤➤➤➤ Token validation failed for user: {}", username);
            }
        } else {
            LOG.warn("➤➤➤➤➤➤➤ Skipping authentication setup: user is already authenticated or username is null.");
        }

        chain.doFilter(request, response);
    }
}
