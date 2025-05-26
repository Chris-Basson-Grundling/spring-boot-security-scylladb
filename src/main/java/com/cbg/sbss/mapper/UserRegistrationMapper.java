package com.cbg.sbss.mapper;

import com.cbg.sbss.dto.RegistrationRequestDto;
import com.cbg.sbss.dto.RegistrationResponseDto;
import com.cbg.sbss.entity.User;
import com.cbg.sbss.repository.Dao.RoleDao;
import com.cbg.sbss.service.RoleService;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegistrationMapper {

  private final RoleService roleService;

  public User toEntity(final RegistrationRequestDto registrationRequestDto) {
    final var user = new User();

    user.setEmail(registrationRequestDto.email());
    user.setUsername(registrationRequestDto.username());
    user.setPassword(registrationRequestDto.password());
    user.setRoles(RoleNamesToRoleIds(registrationRequestDto));

    return user;
  }

  public RegistrationResponseDto toResponseDto(final User user) {
    return new RegistrationResponseDto(user.getEmail(), user.getUsername());
  }

  public Set<UUID> RoleNamesToRoleIds(final RegistrationRequestDto registrationRequestDto) {
    if (registrationRequestDto.roles() != null && !registrationRequestDto.roles().isEmpty()) {

      return registrationRequestDto.roles().stream()
          .map(roleName -> roleService.findByName(roleName)
              .getId())
          .collect(Collectors.toSet());
    }

    return getEmptyUuidSet();
  }

  public Set<UUID> getEmptyUuidSet() {
    return Collections.emptySet();
  }

}