package com.cbg.sbss.config;

import com.cbg.sbss.repository.Dao.DaoMapper;
import com.cbg.sbss.repository.Dao.RefreshTokenDao;
import com.cbg.sbss.repository.Dao.RoleDao;
import com.cbg.sbss.repository.Dao.UserDao;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.ProgrammaticDriverConfigLoaderBuilder;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import java.net.InetSocketAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ScyllaDBConfig {

  @Value("${scylla.contactPoints}")
  private String contactPoints;

  @Value("${scylla.port}")
  private int port;

  @Value("${scylla.localDatacenter}")
  private String localDatacenter;

  @Value("${scylla.keyspace}")
  private String keyspace;

  @Bean
  public CqlSession cqlSession() {
    log.info(
        "Initializing ScyllaDB session with contact points: {}, port: {}, datacenter: {}, keyspace: {}",
        contactPoints, port, localDatacenter, keyspace);

    ProgrammaticDriverConfigLoaderBuilder configLoaderBuilder = DriverConfigLoader.programmaticBuilder()
        .withString(DefaultDriverOption.REQUEST_TIMEOUT, "5 seconds")
        .withString(DefaultDriverOption.REQUEST_CONSISTENCY, "LOCAL_QUORUM")
        .withInt(DefaultDriverOption.CONNECTION_POOL_LOCAL_SIZE, 4)
        .withString(DefaultDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, localDatacenter);

    // 1. Create session without keyspace to create keyspace if needed
    try (CqlSession sessionNoKeyspace = CqlSession.builder()
        .addContactPoint(new InetSocketAddress(contactPoints, port))
        .withLocalDatacenter(localDatacenter)
        .withConfigLoader(configLoaderBuilder.build())
        .build()) {
      createKeyspaceIfNotExists(sessionNoKeyspace);
    }

    // 2. Create session with keyspace
    CqlSession session = CqlSession.builder()
        .addContactPoint(new InetSocketAddress(contactPoints, port))
        .withLocalDatacenter(localDatacenter)
        .withConfigLoader(configLoaderBuilder.build())
        .withKeyspace(keyspace)
        .build();

    // 3. Create tables if needed
    createSchemaIfNotExists(session);

    return session;
  }

  private void createKeyspaceIfNotExists(CqlSession session) {
    session.execute("CREATE KEYSPACE IF NOT EXISTS " + keyspace +
        " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
  }

  private void createSchemaIfNotExists(CqlSession session) {
    // Create users table with the "password" column enclosed in double quotes to avoid reserved keyword conflicts
    session.execute(
        "CREATE TABLE IF NOT EXISTS " + keyspace + ".users (" +
            "id UUID PRIMARY KEY, " +
            "username text, " +
            "email text, " +
            "\"password\" text, " +
            "active boolean, " +
            "email_verified boolean, " +
            "roles set<UUID>" +
            ")");

    // Create secondary indexes for username and email
    session.execute("CREATE INDEX IF NOT EXISTS ON " + keyspace + ".users (username)");
    session.execute("CREATE INDEX IF NOT EXISTS ON " + keyspace + ".users (email)");

    // Create roles table
    session.execute(
        "CREATE TABLE IF NOT EXISTS " + keyspace + ".roles (" +
            "id UUID PRIMARY KEY, " +
            "name text" +
            ")");

    // Create secondary index for role name
    session.execute("CREATE INDEX IF NOT EXISTS ON " + keyspace + ".roles (name)");

    // Create refresh_tokens table
    session.execute(
        "CREATE TABLE IF NOT EXISTS " + keyspace + ".refresh_tokens (" +
            "refresh_token text PRIMARY KEY, " +
            "user_id UUID, " +
            "expires_at timestamp, " +
            "revoked boolean" +
            ")");

    // Create secondary indexes for user_id and expires_at
    session.execute("CREATE INDEX IF NOT EXISTS ON " + keyspace + ".refresh_tokens (user_id)");
    session.execute("CREATE INDEX IF NOT EXISTS ON " + keyspace + ".refresh_tokens (expires_at)");
  }

  @Bean
  public DaoMapper daoMapper(CqlSession cqlSession) {
    return DaoMapper.builder(cqlSession).build();
  }

  @Bean
  public UserDao userDao(DaoMapper mapper) {
    return mapper.userDao(CqlIdentifier.fromCql(keyspace));
  }

  @Bean
  public RoleDao roleDao(DaoMapper mapper) {
    return mapper.roleDao(CqlIdentifier.fromCql(keyspace));
  }

  @Bean
  public RefreshTokenDao refreshTokenDao(DaoMapper mapper) {
    return mapper.refreshTokenDao(CqlIdentifier.fromCql(keyspace));
  }

  @Mapper
  public interface UserRoleMapper {

    @DaoFactory
    UserDao userDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    RoleDao roleDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    RefreshTokenDao refreshTokenDao(@DaoKeyspace CqlIdentifier keyspace);
  }
}