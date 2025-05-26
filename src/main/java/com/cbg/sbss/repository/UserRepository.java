package com.cbg.sbss.repository;

import com.cbg.sbss.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  List<User> findAll();

  Optional<User> findById(UUID id);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  User save(User user);

  void delete(User user);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}