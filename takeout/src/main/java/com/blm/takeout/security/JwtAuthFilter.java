package com.blm.takeout.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blm.takeout.service.CustomUserDetailsService;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String path = request.getServletPath();
        logger.debug("Processing request for path: {}", path);

        if (isPublicPath(path)) {
            logger.debug("Path {} is public, skipping authentication", path);
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", authHeader);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header for path: {}", path);
            sendError(response, 401, "Missing or invalid Authorization header");
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            logger.debug("Validating JWT token for path: {}", path);

            if (!jwtUtils.validateToken(jwt)) {
                logger.warn("Invalid JWT token for path: {}", path);
                sendError(response, 401, "Invalid JWT token");
                return;
            }

            String username = jwtUtils.getUsername(jwt);
            logger.debug("Loading user details for username: {}", username);
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.debug("Authentication successful for user: {}", username);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage());
            sendError(response, 401, "Authentication failed: " + e.getMessage());
        }
    }

    private boolean isPublicPath(String path) {
        return path.equals("/") || 
               path.startsWith("/register") || 
               path.startsWith("/login") || 
               path.startsWith("/v3/api-docs/") ||
               path.equals("/error") ||
               path.equals("/shops") ||
               path.equals("/shop") ||
               path.equals("/items") ||
               path.startsWith("/items?") ||
               path.startsWith("/shops?") ||
               path.startsWith("/shop?");
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}