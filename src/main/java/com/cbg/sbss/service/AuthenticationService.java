package com.cbg.sbss.service;

import static java.time.Duration.between;

import com.cbg.sbss.dto.UserDto;
import com.cbg.sbss.entity.AuthTokens;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final RefreshTokenService refreshTokenService;
  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RoleService roleService;

  public AuthTokens authenticate(final String username, final String password) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));
    final UserDto user = userService.findByUsername(username);
    return generateTokens(user);
  }

  public AuthTokens authenticate(final UserDto user) {
    return generateTokens(user);
  }

  private AuthTokens generateTokens(final UserDto user) {
    List<String> roleNames = user.getRoleNames().stream()
        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role).toList();

    final var accessToken = jwtService.generateToken(user.getId(), roleNames);
    final var refreshTokenEntity = refreshTokenService.createRefreshToken(user.getId());
    return new AuthTokens(accessToken, refreshTokenEntity.getToken(),
        between(Instant.now(), refreshTokenEntity.getExpiresAt()));
  }

  public AuthTokens refreshToken(final String refreshToken) {
    final var refreshTokenEntity = refreshTokenService.verifyRefreshToken(refreshToken);
    final var user = userService.findById(refreshTokenEntity.getUserId());
    List<String> roleNames = user.getRoleNames().stream()
        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role).toList();

    final var newAccessToken = jwtService.generateToken(user.getId(), roleNames);
    return new AuthTokens(newAccessToken, refreshToken,
        between(Instant.now(), refreshTokenEntity.getExpiresAt()));
  }

  public void revokeRefreshToken(String refreshToken) {
    refreshTokenService.revokeRefreshToken(refreshToken);
  }
}