package com.cbg.sbss.repository;

import com.cbg.sbss.entity.RefreshToken;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {

  Optional<RefreshToken> findByToken(String token);

  List<RefreshToken> findByUserId(UUID userId);

  RefreshToken save(RefreshToken refreshToken);

  void deleteByToken(String token);

  void deleteAllByUserId(UUID userId);

  void deleteAllExpiredTokens();

  void revokeToken(String token);
}