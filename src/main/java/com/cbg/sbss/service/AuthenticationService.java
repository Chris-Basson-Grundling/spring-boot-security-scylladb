package com.cbg.sbss.service;

import static java.time.Duration.between;

import com.cbg.sbss.dto.UserDto;
import com.cbg.sbss.entity.AuthTokens;
import java.time.Instant;
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

  public AuthTokens authenticate(final String username, final String password) {
    Authentication test = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));
    final UserDto user = userService.findByUsername(username);
    return generateTokens(user);
  }

  public AuthTokens authenticate(final UserDto user) {
    return generateTokens(user);
  }

  private AuthTokens generateTokens(final UserDto user) {
    final var accessToken = jwtService.generateToken(user.getId());
    final var refreshTokenEntity = refreshTokenService.createRefreshToken(user.getId());
    return new AuthTokens(accessToken, refreshTokenEntity.getToken(),
        between(Instant.now(), refreshTokenEntity.getExpiresAt()));
  }

  public AuthTokens refreshToken(final String refreshToken) {
    final var refreshTokenEntity = refreshTokenService.verifyRefreshToken(refreshToken);
    final var newAccessToken = jwtService.generateToken(refreshTokenEntity.getUserId());
    return new AuthTokens(newAccessToken, refreshToken,
        between(Instant.now(), refreshTokenEntity.getExpiresAt()));
  }

  public void revokeRefreshToken(String refreshToken) {
    refreshTokenService.revokeRefreshToken(refreshToken);
  }
}