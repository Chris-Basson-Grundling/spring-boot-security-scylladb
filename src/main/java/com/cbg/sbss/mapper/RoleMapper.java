package com.cbg.sbss.mapper;

import com.cbg.sbss.dto.RoleDto;
import com.cbg.sbss.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

  RoleDto toDto(Role role);

  Role toEntity(RoleDto roleDto);
}