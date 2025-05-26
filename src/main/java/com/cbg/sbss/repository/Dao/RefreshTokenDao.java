package com.cbg.sbss.repository.Dao;

import com.cbg.sbss.entity.RefreshToken;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import java.util.Optional;
import java.util.UUID;

@Dao
public interface RefreshTokenDao {

  @Select
  Optional<RefreshToken> findByToken(String token);

  @Query("SELECT * FROM ${keyspaceId}.refresh_tokens WHERE user_id = :userId ALLOW FILTERING")
  PagingIterable<RefreshToken> findByUserId(UUID userId);

  @Query("SELECT * FROM ${keyspaceId}.refresh_tokens WHERE expires_at < toTimestamp(now()) ALLOW FILTERING")
  PagingIterable<RefreshToken> findAllExpired();

  @Insert
  void save(RefreshToken refreshToken);

  @Update
  void update(RefreshToken refreshToken);

  @Delete
  void delete(RefreshToken refreshToken);
}