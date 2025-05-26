package com.cbg.sbss.entity;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@CqlName("users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

  @PartitionKey
  @CqlName("id")
  private UUID id;

  @CqlName("username")
  private String username;

  @CqlName("email")
  private String email;

  @CqlName("\"password\"")
  private String password;

  @CqlName("active")
  private boolean active;

  @CqlName("email_verified")
  private boolean emailVerified;

  @CqlName("roles")
  @Builder.Default
  private Set<UUID> roles = new HashSet<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final var user = (User) o;
    return id.equals(user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}