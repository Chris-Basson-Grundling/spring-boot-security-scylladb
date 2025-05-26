package com.cbg.sbss.service.impl;

import com.cbg.sbss.dto.RoleDto;
import com.cbg.sbss.entity.Role;
import com.cbg.sbss.mapper.RoleMapper;
import com.cbg.sbss.repository.RoleRepository;
import com.cbg.sbss.service.RoleService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;

  private final RoleMapper roleMapper;

  @Override
  public List<RoleDto> findAll() {
    return roleRepository.findAll().stream().map(roleMapper::toDto).collect(Collectors.toList());
  }

  @Override
  public RoleDto findById(UUID id) {
    return roleRepository.findById(id).map(roleMapper::toDto)
        .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
  }

  @Override
  public RoleDto findByName(String name) {
    return roleRepository.findByName(name).map(roleMapper::toDto)
        .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));
  }

  @Override
  public RoleDto save(RoleDto roleDto) {
    if (roleRepository.existsByName(roleDto.getName())) {
      throw new RuntimeException("Role name already exists: " + roleDto.getName());
    }
    Role role = roleMapper.toEntity(roleDto);
    Role savedRole = roleRepository.save(role);
    return roleMapper.toDto(savedRole);
  }

  @Override
  public RoleDto update(RoleDto RoleDto) {
    Role existingRole = roleRepository.findById(RoleDto.getId())
        .orElseThrow(() -> new RuntimeException("Role not found with id: " + RoleDto.getId()));

    existingRole.setName(RoleDto.getName());
    Role updatedRole = roleRepository.save(existingRole);
    return roleMapper.toDto(updatedRole);
  }

  @Override
  public void delete(UUID id) {
    roleRepository.findById(id).ifPresent(roleRepository::delete);
  }
}