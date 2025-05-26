package com.cbg.sbss.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
public class UserDto {

  private UUID id;
  private String username;
  private String email;
  private String password;
  private boolean active;
  private boolean emailVerified;
  @Builder.Default
  private Set<UUID> roles = new HashSet<>();
  @Builder.Default
  private Set<String> roleNames = new HashSet<>();
}