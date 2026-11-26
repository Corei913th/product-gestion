package eadl.productgestion.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

     private final JwtService jwtService ;

     private final UserAuthenticationService userAuthenticationService;

     private final TokenBlacklistService tokenBlacklistService;

     private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = extractTokenFromHeader(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (tokenBlacklistService.isRevoked(token)) {
            log.warn("Token révoqué utilisé pour la requête {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // Valider le token et extraire l' email de l' utilisateur
        try {
            String userEmail = jwtService.getUsernameFromToken(token);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                // Charger les UserDetails(authUser) depuis la base
                AuthUser authUser = userAuthenticationService.loadUserByUsername(userEmail);

                // Créer l'objet d'authentification Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                authUser,
                                null,
                                authUser.getAuthorities()
                        );
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Injecter dans le SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Utilisateur {} authentifié via JWT pour {}",
                        userEmail, request.getRequestURI());
            }

        } catch (Exception e) {
            log.warn("JWT invalide pour la requête {} : {}", request.getRequestURI(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }


    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
