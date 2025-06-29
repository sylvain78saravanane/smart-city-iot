package com.ensitech.smart_city_iot.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class JwtAuthenticatonFilter extends OncePerRequestFilter {
    @Autowired
    private JWTService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Vérifier si le header Authorization existe et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extraire le token du header
            final String jwt = authHeader.substring(7); // Supprimer "Bearer "

            // Vérifier si le token est valide
            if (jwtService.isTokenValid(jwt)) {
                // Extraire les informations du token
                String email = jwtService.extractEmail(jwt);
                String role = jwtService.extractRole(jwt);
                Long userId = jwtService.extractUserId(jwt);

                log.debug("Token valide pour l'utilisateur: {} avec le rôle: {}", email, role);

                // Créer l'objet d'authentification Spring Security
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, // Principal (identifiant de l'utilisateur)
                        null,  // Credentials (pas besoin avec JWT)
                        Collections.singletonList(authority) // Autorités/rôles
                );

                // Ajouter les détails de la requête
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Définir l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Ajouter l'ID utilisateur dans les attributs de la requête pour un accès facile
                request.setAttribute("userId", userId);
                request.setAttribute("userRole", role);
                request.setAttribute("userEmail", email);
            } else {
                log.debug("Token JWT invalide ou expiré");
            }

        } catch (Exception e) {
            log.error("Erreur lors de la validation du token JWT: {}", e.getMessage());
        }

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Ne pas filtrer les routes publiques
        return path.equals("/api/v1/login") ||
                path.equals("/api/v1/admin/login") ||
                path.equals("/api/v1/gestionnaire/login") ||
                (path.equals("/api/v1/utilisateurs") && "POST".equals(request.getMethod()));
    }
}
