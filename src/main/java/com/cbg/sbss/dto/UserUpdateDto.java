package com.cbg.sbss.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateDto {

  private UUID id;
  private String username;
  private String email;
  private boolean active;
  private boolean emailVerified;
  @Builder.Default
  private Set<String> roles = new HashSet<>();
}