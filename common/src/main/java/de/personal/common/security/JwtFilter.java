package de.personal.common.security;

import de.personal.common.exception.JwtExceptionHandler;
import de.personal.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Intercepts requests and sets authentication context if token is valid.
 */
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtExceptionHandler jwtExceptionHandler;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public JwtFilter(JwtUtil jwtUtil,
                     JwtExceptionHandler jwtExceptionHandler) {
        this.jwtUtil = jwtUtil;
        this.jwtExceptionHandler = jwtExceptionHandler;
    }

    // Skip processing for endpoints under /auth/**
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return antPathMatcher.match("/auth/**", path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Extract JWT from Authorization header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // get string after "Bearer "
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                jwtExceptionHandler.handleInvalidToken(response, e);
                return; // Stop filter chain here
            }
        }

        // Set security context if token is valid and no auth is set yet
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(token, username)) {
                // Extract role string and convert to GrantedAuthority
                String role = jwtUtil.extractUserRole(token).name();
                var authorities = List.of((GrantedAuthority) () -> role); // lambda used as GrantedAuthority

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
