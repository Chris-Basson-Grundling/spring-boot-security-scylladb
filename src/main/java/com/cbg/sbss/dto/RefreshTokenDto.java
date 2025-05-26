package com.cbg.sbss.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenDto {

  private String token;
  private UUID userId;
  private Instant expiresAt;
  private boolean revoked;
}