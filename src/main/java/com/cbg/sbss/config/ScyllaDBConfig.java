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
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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

  @Autowired
  private Environment environment;

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
        .withLocalDatacenter(localDatacenter).withConfigLoader(configLoaderBuilder.build())
        .build()) {
      createKeyspaceIfNotExists(sessionNoKeyspace);
    }

    // 2. Create session with keyspace
    CqlSession session = CqlSession.builder()
        .addContactPoint(new InetSocketAddress(contactPoints, port))
        .withLocalDatacenter(localDatacenter).withConfigLoader(configLoaderBuilder.build())
        .withKeyspace(keyspace).build();

    // 3. Create tables if needed
    createSchemaIfNotExists(session);

    // 4. Create admin roles and admin and user if in dev and test profile
    if (isDevOrTestProfileActive()) {
      createAdminRolesAndUserIfNotExists(session);
    }

    return session;
  }

  private void createKeyspaceIfNotExists(CqlSession session) {
    session.execute("CREATE KEYSPACE IF NOT EXISTS " + keyspace
        + " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
  }

  private void createSchemaIfNotExists(CqlSession session) {
    session.execute("CREATE TABLE IF NOT EXISTS " + keyspace + ".users (" + "id UUID PRIMARY KEY, "
        + "username text, " + "email text, " + "\"password\" text, " + // avoid reserved keyword
        "active boolean, " + "email_verified boolean, " + "roles set<UUID>)");

    session.execute("CREATE INDEX IF NOT EXISTS ON " + keyspace + ".users (username)");
    session.execute("CREATE INDEX IF NOT EXISTS ON " + keyspace + ".users (email)");

    session.execute("CREATE TABLE IF NOT EXISTS " + keyspace + ".roles (" + "id UUID PRIMARY KEY, "
        + "name text)");

    session.execute("CREATE INDEX IF NOT EXISTS ON " + keyspace + ".roles (name)");

    session.execute("CREATE TABLE IF NOT EXISTS " + keyspace + ".refresh_tokens ("
        + "refresh_token text PRIMARY KEY, " + "user_id UUID, " + "expires_at timestamp, "
        + "revoked boolean)");

    session.execute("CREATE INDEX IF NOT EXISTS ON " + keyspace + ".refresh_tokens (user_id)");
    session.execute("CREATE INDEX IF NOT EXISTS ON " + keyspace + ".refresh_tokens (expires_at)");
  }

  /**
   * DEV PROFILE ONLY: Ensures an admin role and admin user exist for local/dev.
   */
  private void createAdminRolesAndUserIfNotExists(CqlSession session) {
    try {
      // Create USER role if not exists
      UUID userRoleId = getOrCreateRole(session, "USER");
      // Create ADMIN role if not exists
      UUID adminRoleId = getOrCreateRole(session, "ADMIN");

      // Create user1 if not exists (user role)
      createUserIfNotExists(session, "testuser1", "testuser1@localhost", "testuser123", userRoleId);

      // Create admin if not exists (admin role)
      createUserIfNotExists(session, "admin", "admin@localhost", "admin123", adminRoleId);

    } catch (Exception e) {
      log.error("Error creating admin role/user: ", e);
    }
  }

  private UUID getOrCreateRole(CqlSession session, String roleName) {
    Row row = session.execute(
        "SELECT id FROM " + keyspace + ".roles WHERE name = ? ALLOW FILTERING", roleName).one();
    if (row != null) {
      return row.getUuid("id");
    }
    UUID roleId = UUID.randomUUID();
    session.execute("INSERT INTO " + keyspace + ".roles (id, name) VALUES (?, ?)", roleId,
        roleName);
    log.info("Created {} role with id {}", roleName, roleId);
    return roleId;
  }

  private void createUserIfNotExists(CqlSession session, String username, String email,
      String rawPassword, UUID roleId) {
    Row row = session.execute(
        "SELECT id FROM " + keyspace + ".users WHERE username = ? ALLOW FILTERING", username).one();
    if (row != null) {
      log.info("User already exists with username '{}'", username);
      return;
    }
    UUID userId = UUID.randomUUID();
    String password = encodePassword(rawPassword);
    session.execute("INSERT INTO " + keyspace
            + ".users (id, username, email, \"password\", active, email_verified, roles) VALUES (?, ?, ?, ?, ?, ?, ?)",
        userId, username, email, password, true, true, Collections.singleton(roleId));
    log.info("Created user with username '{}', password '{}'", username, rawPassword);
  }

  private String encodePassword(String password) {
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    return encoder.encode(password);
  }

  private boolean isDevOrTestProfileActive() {
    return environment.acceptsProfiles(Profiles.of("dev", "test"));
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