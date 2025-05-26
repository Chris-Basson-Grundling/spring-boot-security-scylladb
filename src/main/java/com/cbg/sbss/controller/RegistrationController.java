package com.cbg.sbss.controller;

import com.cbg.sbss.dto.RegistrationRequestDto;
import com.cbg.sbss.dto.RegistrationResponseDto;
import com.cbg.sbss.mapper.UserRegistrationMapper;
import com.cbg.sbss.service.EmailVerificationService;
import com.cbg.sbss.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {

  private final UserRegistrationService userRegistrationService;
  private final EmailVerificationService emailVerificationService;
  private final UserRegistrationMapper userRegistrationMapper;

  @PostMapping("/sign-up")
  public ResponseEntity<RegistrationResponseDto> registerUser(
      @Valid @RequestBody final RegistrationRequestDto registrationDTO) {

    return ResponseEntity.ok(
        registerUserAndSendOtp(registrationDTO));
  }

  @PostMapping("/register-user")
  public ResponseEntity<?> registerUser(
      @Valid @RequestBody final RegistrationRequestDto registrationDTO,
      Authentication authentication) {
    boolean isAuthenticated = authentication != null && authentication.isAuthenticated();

    if (!isAuthenticated) {
      return ResponseEntity.status(403).body("Access denied. User is not authenticated.");
    }

    boolean isAdmin = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"));

    if (isAdmin) {
      return ResponseEntity.ok(registerUserAndSendOtp(registrationDTO));
    }

    registrationDTO.roles().removeIf(role -> role != null && role.equalsIgnoreCase("ROLE_ADMIN"));

    return ResponseEntity.ok(registerUserAndSendOtp(registrationDTO));
  }

  private RegistrationResponseDto registerUserAndSendOtp(RegistrationRequestDto registrationUser) {
    final var registeredUser = userRegistrationService.registerUser(
        userRegistrationMapper.toEntity(registrationUser));

    emailVerificationService.sendEmailVerificationOtp(registeredUser.getId(),
        registeredUser.getEmail());

    return userRegistrationMapper.toResponseDto(registeredUser);
  }
}