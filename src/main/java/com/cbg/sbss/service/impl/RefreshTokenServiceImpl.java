package com.cbg.sbss.service.impl;

import com.cbg.sbss.dto.RefreshTokenDto;
import com.cbg.sbss.entity.RefreshToken;
import com.cbg.sbss.mapper.RefreshTokenMapper;
import com.cbg.sbss.repository.RefreshTokenRepository;
import com.cbg.sbss.service.RefreshTokenService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  private final RefreshTokenMapper refreshTokenMapper;

  @Value("${jwt.refresh-token-ttl}")
  private String refreshTokenTtl;

  @Override
  public RefreshTokenDto createRefreshToken(UUID userId) {
    long ttlInDays = parseTtl(refreshTokenTtl);

    RefreshToken refreshToken = RefreshToken.builder().token(UUID.randomUUID().toString())
        .userId(userId).expiresAt(Instant.now().plus(ttlInDays, ChronoUnit.DAYS)).revoked(false)
        .build();

    RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
    return refreshTokenMapper.toDto(savedToken);
  }

  @Override
  public RefreshTokenDto verifyRefreshToken(String token) {
    RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
        .orElseThrow(() -> new RuntimeException("Refresh token not found"));

    if (refreshToken.isRevoked()) {
      throw new RuntimeException("Refresh token has been revoked");
    }

    if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
      refreshTokenRepository.deleteByToken(token);
      throw new RuntimeException("Refresh token has expired");
    }

    return refreshTokenMapper.toDto(refreshToken);
  }

  @Override
  public void revokeRefreshToken(String token) {
    refreshTokenRepository.revokeToken(token);
  }

  @Override
  public void revokeAllUserTokens(UUID userId) {
    refreshTokenRepository.deleteAllByUserId(userId);
  }

  @Override
  public List<RefreshTokenDto> findUserRefreshTokens(UUID userId) {
    return refreshTokenRepository.findByUserId(userId).stream().map(refreshTokenMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public void cleanupExpiredTokens() {
    refreshTokenRepository.deleteAllExpiredTokens();
  }

  private long parseTtl(String ttl) {
    if (ttl.endsWith("d")) {
      return Long.parseLong(ttl.substring(0, ttl.length() - 1));
    }
    // Default to 1 day if format is not recognized
    return 1;
  }
}