package com.cbg.sbss.mapper;

import com.cbg.sbss.dto.UserDto;
import com.cbg.sbss.dto.UserProfileDto;
import com.cbg.sbss.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "roleNames", ignore = true)
  @Mapping(target = "roles", ignore = true)
  UserDto toDto(User user);

  @Mapping(target = "roles", ignore = true)
  User toEntity(UserDto userDto);

  @Mapping(target = "email", source = "user.email")
  @Mapping(target = "username", source = "user.username")
  @Mapping(target = "emailVerified", source = "user.emailVerified")
  UserProfileDto toUserProfileDto(UserDto user);

}
