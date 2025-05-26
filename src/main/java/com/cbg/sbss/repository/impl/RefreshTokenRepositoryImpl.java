package com.cbg.sbss.repository.impl;

import com.cbg.sbss.entity.RefreshToken;
import com.cbg.sbss.repository.Dao.RefreshTokenDao;
import com.cbg.sbss.repository.RefreshTokenRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

  private final RefreshTokenDao refreshTokenDao;

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenDao.findByToken(token);
  }

  @Override
  public List<RefreshToken> findByUserId(UUID userId) {
    return refreshTokenDao.findByUserId(userId).all();
  }

  @Override
  public RefreshToken save(RefreshToken refreshToken) {
    refreshTokenDao.save(refreshToken);
    return refreshToken;
  }

  @Override
  public void deleteByToken(String token) {
    findByToken(token).ifPresent(refreshTokenDao::delete);
  }

  @Override
  public void deleteAllByUserId(UUID userId) {
    for (RefreshToken token : refreshTokenDao.findByUserId(userId)) {
      refreshTokenDao.delete(token);
    }
  }

  @Override
  public void deleteAllExpiredTokens() {
    for (RefreshToken token : refreshTokenDao.findAllExpired()) {
      refreshTokenDao.delete(token);
    }
  }

  @Override
  public void revokeToken(String token) {
    findByToken(token).ifPresent(refreshToken -> {
      refreshToken.setRevoked(true);
      refreshTokenDao.update(refreshToken);
    });
  }
}