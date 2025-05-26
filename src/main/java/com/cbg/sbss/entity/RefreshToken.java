package com.cbg.sbss.entity;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@CqlName("refresh_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

  @PartitionKey
  @CqlName("refresh_token")
  private String token;

  @CqlName("user_id")
  private UUID userId;

  @CqlName("expires_at")
  private Instant expiresAt;

  @CqlName("revoked")
  private boolean revoked;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final var user = (RefreshToken) o;
    return userId.equals(user.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId);
  }

}