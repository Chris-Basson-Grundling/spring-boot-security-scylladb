package com.cbg.sbss.service.impl;

import com.cbg.sbss.dto.UserDto;
import com.cbg.sbss.dto.UserUpdateDto;
import com.cbg.sbss.entity.Role;
import com.cbg.sbss.entity.User;
import com.cbg.sbss.mapper.UserMapper;
import com.cbg.sbss.repository.RoleRepository;
import com.cbg.sbss.repository.UserRepository;
import com.cbg.sbss.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  private final UserMapper userMapper;

  private final PasswordEncoder passwordEncoder;

  @Override
  public List<UserDto> findAll() {
    List<User> users = userRepository.findAll();
    return users.stream().map(user -> {
      UserDto dto = userMapper.toDto(user);
      List<Role> roles = roleRepository.findAllById(user.getRoles());
      dto.setRoleNames(roles.stream().map(Role::getName).collect(Collectors.toSet()));
      return dto;
    }).collect(Collectors.toList());
  }

  @Override
  public UserDto findById(UUID id) {
    return userRepository.findById(id).map(user -> {
      UserDto dto = userMapper.toDto(user);
      List<Role> roles = roleRepository.findAllById(user.getRoles());
      dto.setRoleNames(roles.stream().map(Role::getName).collect(Collectors.toSet()));
      return dto;
    }).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
  }

  @Override
  public UserDto findByUsername(String username) {
    return userRepository.findByUsername(username).map(user -> {
      UserDto dto = userMapper.toDto(user);
      List<Role> roles = roleRepository.findAllById(user.getRoles());
      dto.setRoleNames(roles.stream().map(Role::getName).collect(Collectors.toSet()));
      return dto;
    }).orElseThrow(() -> new RuntimeException("User not found with username: " + username));
  }

  @Override
  public Optional<UserDto> findByEmail(String email) {
    return userRepository.findByEmail(email).map(user -> {
      UserDto dto = userMapper.toDto(user);
      List<Role> roles = roleRepository.findAllById(user.getRoles());
      dto.setRoleNames(roles.stream().map(Role::getName).collect(Collectors.toSet()));
      return dto;
    });
  }

  @Override
  public UserDto save(UserDto UserDto) {
    User user = userMapper.toEntity(UserDto);
    user.setPassword(passwordEncoder.encode(UserDto.getPassword()));
    user.setActive(true);
    // By default, set email as not verified
    user.setEmailVerified(false);

    Set<UUID> roleIds = UserDto.getRoleNames().stream().map(
            roleName -> roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)).getId())
        .collect(Collectors.toSet());
    user.setRoles(roleIds);

    User savedUser = userRepository.save(user);

    UserDto result = userMapper.toDto(savedUser);
    result.setRoles(UserDto.getRoles()); // Set the original role names
    return result;
  }

  @Override
  public UserDto update(UserUpdateDto UserUpdateDto) {
    User existingUser = userRepository.findById(UserUpdateDto.getId()).orElseThrow(
        () -> new RuntimeException("User not found with id: " + UserUpdateDto.getId()));

    existingUser.setUsername(UserUpdateDto.getUsername());
    existingUser.setEmail(UserUpdateDto.getEmail());
    existingUser.setActive(UserUpdateDto.isActive());
    existingUser.setEmailVerified(UserUpdateDto.isEmailVerified());

    // Convert role names to UUIDs
    if (UserUpdateDto.getRoles() != null && !UserUpdateDto.getRoles().isEmpty()) {
      Set<UUID> roleIds = UserUpdateDto.getRoles().stream().map(
              roleName -> roleRepository.findByName(roleName)
                  .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)).getId())
          .collect(Collectors.toSet());
      existingUser.setRoles(roleIds);
    }

    User updatedUser = userRepository.save(existingUser);

    UserDto result = userMapper.toDto(updatedUser);
    List<Role> roles = roleRepository.findAllById(updatedUser.getRoles());
    result.setRoleNames(roles.stream().map(Role::getName).collect(Collectors.toSet()));
    return result;
  }

  @Override
  public void delete(UUID id) {
    userRepository.findById(id).ifPresent(userRepository::delete);
  }

  @Override
  public void verifyEmail(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    user.setEmailVerified(true);
    userRepository.save(user);
  }
}