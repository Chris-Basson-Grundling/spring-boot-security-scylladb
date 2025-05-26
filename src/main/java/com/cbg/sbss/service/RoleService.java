package com.cbg.sbss.service;

import com.cbg.sbss.dto.RoleDto;
import java.util.List;
import java.util.UUID;

public interface RoleService {

  List<RoleDto> findAll();

  RoleDto findById(UUID id);

  RoleDto findByName(String name);

  RoleDto save(RoleDto RoleDto);

  RoleDto update(RoleDto RoleDto);

  void delete(UUID id);
}