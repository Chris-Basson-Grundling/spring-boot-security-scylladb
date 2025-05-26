package com.cbg.sbss.repository.impl;

import com.cbg.sbss.entity.User;
import com.cbg.sbss.repository.Dao.UserDao;
import com.cbg.sbss.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserDao userDao;

  @Override
  public List<User> findAll() {
    return userDao.findAll().all();
  }

  @Override
  public Optional<User> findById(UUID id) {
    return userDao.findById(id);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userDao.findByUsername(username);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userDao.findByEmail(email);
  }

  @Override
  public User save(User user) {
    if (user.getId() == null) {
      user.setId(UUID.randomUUID());
    }
    userDao.save(user);
    return user;
  }

  @Override
  public void delete(User user) {
    userDao.delete(user);
  }

  @Override
  public boolean existsByEmail(String email) {
    return findByEmail(email).isPresent();
  }

  @Override
  public boolean existsByUsername(String username) {
    return findByUsername(username).isPresent();
  }
}