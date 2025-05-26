package com.cbg.sbss.service;

import com.cbg.sbss.dto.RefreshTokenDto;
import java.util.List;
import java.util.UUID;

public interface RefreshTokenService {

  RefreshTokenDto createRefreshToken(UUID userId);

  RefreshTokenDto verifyRefreshToken(String token);

  void revokeRefreshToken(String token);

  void revokeAllUserTokens(UUID userId);

  List<RefreshTokenDto> findUserRefreshTokens(UUID userId);

  void cleanupExpiredTokens();
}