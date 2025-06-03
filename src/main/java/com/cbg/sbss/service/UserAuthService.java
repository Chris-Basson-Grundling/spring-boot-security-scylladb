package com.cbg.sbss.service;

import static com.cbg.sbss.exception.ErrorType.EMAIL_VERIFICATION_REQUIRED;
import static com.cbg.sbss.exception.ProblemDetailBuilder.forStatusAndDetail;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.cbg.sbss.exception.RestErrorResponseException;
import com.cbg.sbss.repository.UserRepository;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthService implements UserDetailsService {

  private final UserRepository userRepository;
  private final RoleService roleService;

  @Override
  public UserDetails loadUserByUsername(final String username) {
    return userRepository.findByUsername(username).map(user -> {
      if (!user.isEmailVerified()) {
        throw new RestErrorResponseException(
            forStatusAndDetail(UNAUTHORIZED, "Email verification required").withProperty("email",
                user.getEmail()).withErrorType(EMAIL_VERIFICATION_REQUIRED).build());
      }

      Set<GrantedAuthority> authorities = getAuthoritiesFromRoleIds(user.getRoles());
      return org.springframework.security.core.userdetails.User.builder().username(username)
          .password(user.getPassword()).authorities(authorities).build();
    }).orElseThrow(() -> new UsernameNotFoundException(
        "User with username [%s] not found".formatted(username)));
  }

  private Set<GrantedAuthority> getAuthoritiesFromRoleIds(Set<UUID> roleIds) {
    if (roleIds == null || roleIds.isEmpty()) {
      return Set.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    return roleIds.stream().map(roleService::findById).map(role -> {
      String name = role.getName();
      // Always ensure ROLE_ prefix
      return name != null && name.startsWith("ROLE_") ? name : "ROLE_" + name;
    }).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
  }
}