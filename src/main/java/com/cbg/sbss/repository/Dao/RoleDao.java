package com.cbg.sbss.repository.Dao;

import com.cbg.sbss.entity.Role;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Dao
public interface RoleDao {

  @Select
  PagingIterable<Role> findAll();

  @Select
  Optional<Role> findById(UUID id);

  @Query("SELECT * FROM ${keyspaceId}.roles WHERE name = :name ALLOW FILTERING")
  Optional<Role> findByName(String name);

  @Insert
  void save(Role role);

  @Update
  void update(Role role);

  @Delete
  void delete(Role role);
}