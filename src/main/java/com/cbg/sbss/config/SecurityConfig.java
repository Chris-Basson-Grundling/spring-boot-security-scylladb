package com.cbg.sbss.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.cbg.sbss.config.security.BearerTokenAccessDeniedHandler;
import com.cbg.sbss.config.security.BearerTokenAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
      final HttpSecurity http,
      final BearerTokenAuthenticationEntryPoint authenticationEntryPoint,
      final BearerTokenAccessDeniedHandler accessDeniedHandler
  ) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/roles/**").authenticated()
            .requestMatchers("/", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**")
            .permitAll()
            .anyRequest().authenticated()

        )
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .oauth2ResourceServer(server -> server
            .jwt(Customizer.withDefaults())
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        )
        .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      final AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final var config = new CorsConfiguration();
    config.addAllowedOrigin("http://localhost:3000");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.setAllowCredentials(true);

    final var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);

    return source;
  }

}