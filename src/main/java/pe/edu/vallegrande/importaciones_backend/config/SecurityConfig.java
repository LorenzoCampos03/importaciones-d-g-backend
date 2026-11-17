package pe.edu.vallegrande.importaciones_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pe.edu.vallegrande.importaciones_backend.service.CustomUserDetailsService;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig implements WebMvcConfigurer {

     @Autowired
     private JwtAuthenticationFilter jwtAuthFilter;

     @Autowired
     private CustomUserDetailsService userDetailsService;

     @Bean
     public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder();
     }

     @Bean
     public AuthenticationProvider authenticationProvider() {
          DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
          authProvider.setUserDetailsService(userDetailsService);
          authProvider.setPasswordEncoder(passwordEncoder());
          return authProvider;
     }

     @Bean
     public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
          return config.getAuthenticationManager();
     }

     @Bean
     public CorsConfigurationSource corsConfigurationSource() {
          CorsConfiguration configuration = new CorsConfiguration();

          // CONFIGURACIÓN COMPLETAMENTE ABIERTA - PERMITIR TODO
          configuration.setAllowedOriginPatterns(Arrays.asList("*"));
          configuration.setAllowedOrigins(Arrays.asList("*"));
          configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
          configuration.setAllowedHeaders(Arrays.asList("*"));
          configuration.setExposedHeaders(Arrays.asList("*"));
          configuration.setAllowCredentials(false); // Cambiar a false para permitir wildcard origins
          configuration.setMaxAge(3600L);

          UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          source.registerCorsConfiguration("/**", configuration);
          return source;
     }

     // Configuración adicional de CORS a nivel de WebMvc
     @Override
     public void addCorsMappings(CorsRegistry registry) {
          registry.addMapping("/**")
                  .allowedOriginPatterns("*")
                  .allowedMethods("*")
                  .allowedHeaders("*")
                  .allowCredentials(false)
                  .maxAge(3600);
     }

     @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
          http
                  .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                  .csrf(csrf -> csrf.disable()).authorizeHttpRequests(authz -> authz
                          .requestMatchers("/api/v1/auth/**").permitAll()
                          .requestMatchers("/api/v1/migration/**").permitAll()
                          .requestMatchers("/api/v1/debug/**").permitAll()
                          .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                          .requestMatchers("/actuator/**").permitAll()
                          .requestMatchers("/api/v1/client/**").hasAnyRole("Cliente", "Administrador", "Vendedor")
                          .requestMatchers("/api/v1/employee/**").hasAnyRole("Administrador", "Vendedor")
                          .requestMatchers("/api/v1/admin/**").hasRole("Administrador")
                          .requestMatchers("/api/v1/products/**").hasAnyRole("Cliente", "Administrador", "Vendedor")
                          .requestMatchers("/api/v1/purchases/**").hasAnyRole("Administrador", "Vendedor")
                          .requestMatchers("/api/v1/sale/**").hasAnyRole("Cliente", "Administrador", "Vendedor")
                          .requestMatchers("/api/v1/office/**").hasAnyRole("Administrador", "Vendedor")

                          .anyRequest().authenticated())
                  .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                  .authenticationProvider(authenticationProvider())
                  .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

          return http.build();
     }
}
