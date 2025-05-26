package com.cbg.sbss.service;

import com.cbg.sbss.dto.UserDto;
import com.cbg.sbss.dto.UserUpdateDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

  List<UserDto> findAll();

  UserDto findById(UUID id);

  UserDto findByUsername(String username);

  Optional<UserDto> findByEmail(String email);

  UserDto save(UserDto userDto);

  UserDto update(UserUpdateDto userUpdateDto);

  void delete(UUID id);

  void verifyEmail(UUID userId);
}