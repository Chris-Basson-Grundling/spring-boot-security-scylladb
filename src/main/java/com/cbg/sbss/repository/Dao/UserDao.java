package com.cbg.sbss.repository.Dao;

import com.cbg.sbss.entity.User;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import java.util.Optional;
import java.util.UUID;

@Dao
public interface UserDao {

  @Select
  PagingIterable<User> findAll();

  @Select
  Optional<User> findById(UUID id);

  @Query("SELECT * FROM ${keyspaceId}.users WHERE username = :username ALLOW FILTERING")
  Optional<User> findByUsername(String username);

  @Query("SELECT * FROM ${keyspaceId}.users WHERE email = :email")
  Optional<User> findByEmail(String email);

  @Insert
  void save(User user);

  @Update
  void update(User user);

  @Delete
  void delete(User user);
}