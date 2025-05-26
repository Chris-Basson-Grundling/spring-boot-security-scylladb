package com.cbg.sbss.repository;

import com.cbg.sbss.entity.Role;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleRepository {

  List<Role> findAll();

  Optional<Role> findById(UUID id);

  Optional<Role> findByName(String name);

  List<Role> findAllById(Set<UUID> ids);

  Role save(Role role);

  void delete(Role role);

  boolean existsByName(String name);
}