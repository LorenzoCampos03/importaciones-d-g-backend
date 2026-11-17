package pe.edu.vallegrande.importaciones_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.edu.vallegrande.importaciones_backend.service.CustomUserDetailsService;
import pe.edu.vallegrande.importaciones_backend.service.JwtService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

     @Autowired
     private JwtService jwtService;

     @Autowired
     private CustomUserDetailsService userDetailsService;

     @Override
     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
               throws ServletException, IOException {

          // Permitir siempre el acceso a las rutas públicas sin verificar el token
          String requestURI = request.getRequestURI();
          if (isPublicEndpoint(requestURI)) {
               filterChain.doFilter(request, response);
               return;
          }

          final String authHeader = request.getHeader("Authorization");

          // Si no hay token o no empieza con "Bearer ", continuar sin autenticar
          if (authHeader == null || !authHeader.startsWith("Bearer ")) {
               filterChain.doFilter(request, response);
               return;
          }

          // Extraer el token eliminando "Bearer "
          String jwt = authHeader.substring(7).trim();

          // Validación básica del formato JWT (debe tener exactamente 2 puntos)
          if (jwt.chars().filter(ch -> ch == '.').count() != 2) {
               logger.error("Error al procesar JWT: JWT strings must contain exactly 2 period characters. Found: "
                         + jwt.chars().filter(ch -> ch == '.').count());
               filterChain.doFilter(request, response);
               return;
          }

          try {
               String username = jwtService.extractUsername(jwt);

               // Solo procesar si hay un nombre de usuario y no hay autenticación previa
               if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String role = jwtService.extractRole(jwt);

                    // Crear la lista de autoridades basada en el rol extraído del token
                    List<SimpleGrantedAuthority> authorities = Arrays.asList(
                              new SimpleGrantedAuthority("ROLE_" + role));

                    // Validar token y autenticar si es válido
                    if (jwtService.validateToken(jwt, username)) {
                         UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                   username,
                                   null,
                                   authorities);
                         authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                         SecurityContextHolder.getContext().setAuthentication(authToken);
                         logger.info("Usuario autenticado: " + username + " con rol: " + role);
                    }
               }
          } catch (Exception e) {
               logger.error("Error al procesar JWT: " + e.getMessage());
          }

          filterChain.doFilter(request, response);
     }

     /**
      * Verifica si la URI solicitada corresponde a un endpoint público que no
      * requiere autenticación
      */
     private boolean isPublicEndpoint(String uri) {
          return uri.startsWith("/api/v1/auth/") ||
                    uri.startsWith("/swagger-ui/") ||
                    uri.startsWith("/v3/api-docs/") ||
                    uri.contains("swagger-ui.html") ||
                    uri.startsWith("/actuator/") ||
                    uri.startsWith("/api/v1/migration/") ||
                    uri.startsWith("/api/v1/debug/");
     }
}
