package com.cbg.sbss.repository.Dao;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.mapper.MapperBuilder;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DefaultNullSavingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;

@Mapper
@DefaultNullSavingStrategy(NullSavingStrategy.DO_NOT_SET)
public interface DaoMapper {

  @DaoFactory
  UserDao userDao(@DaoKeyspace CqlIdentifier keyspace);

  @DaoFactory
  RoleDao roleDao(@DaoKeyspace CqlIdentifier keyspace);

  @DaoFactory
  RefreshTokenDao refreshTokenDao(@DaoKeyspace CqlIdentifier keyspace);

  static MapperBuilder<DaoMapper> builder(CqlSession session) {
    return new DaoMapperBuilder(session);
  }
}