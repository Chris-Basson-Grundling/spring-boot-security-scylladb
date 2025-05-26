package com.cbg.sbss.repository.impl;

import com.cbg.sbss.entity.Role;
import com.cbg.sbss.repository.Dao.RoleDao;
import com.cbg.sbss.repository.RoleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

  private final RoleDao roleDao;

  @Override
  public List<Role> findAll() {
    return roleDao.findAll().all();
  }

  @Override
  public Optional<Role> findById(UUID id) {
    return roleDao.findById(id);
  }

  @Override
  public Optional<Role> findByName(String name) {
    return roleDao.findByName(name);
  }

  @Override
  public List<Role> findAllById(Set<UUID> ids) {
    List<Role> roles = new ArrayList<>();
    for (UUID id : ids) {
      roleDao.findById(id).ifPresent(roles::add);
    }
    return roles;
  }

  @Override
  public boolean existsByName(String name) {
    return roleDao.findByName(name).isPresent();
  }

  @Override
  public Role save(Role role) {
    if (role.getId() == null) {
      role.setId(UUID.randomUUID());
    }
    roleDao.save(role);
    return role;
  }

  @Override
  public void delete(Role role) {
    roleDao.delete(role);
  }
}