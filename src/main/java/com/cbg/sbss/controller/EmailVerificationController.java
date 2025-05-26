package com.cbg.sbss.controller;

import static com.cbg.sbss.entity.AuthTokens.REFRESH_TOKEN_COOKIE_NAME;
import static com.cbg.sbss.util.CookieUtil.addCookie;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

import com.cbg.sbss.dto.AuthenticationResponseDto;
import com.cbg.sbss.dto.EmailVerificationRequestDto;
import com.cbg.sbss.service.AuthenticationService;
import com.cbg.sbss.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

  private final EmailVerificationService emailVerificationService;
  private final AuthenticationService authenticationService;

  @PostMapping("/request-verification-email")
  public ResponseEntity<Void> resendVerificationOtp(@RequestParam final String email) {

    emailVerificationService.resendEmailVerificationOtp(email);

    return ResponseEntity.noContent().build();
  }

  @PostMapping("/verify-email")
  public ResponseEntity<AuthenticationResponseDto> verifyOtp(
      @Valid @RequestBody final EmailVerificationRequestDto requestDto) {

    final var verifiedUser = emailVerificationService.verifyEmailOtp(requestDto.email(),
        requestDto.otp());

    final var authTokens = authenticationService.authenticate(verifiedUser);

    return ResponseEntity.ok().header(SET_COOKIE,
            addCookie(REFRESH_TOKEN_COOKIE_NAME, authTokens.refreshToken(),
                authTokens.refreshTokenTtl()).toString())
        .body(new AuthenticationResponseDto(authTokens.accessToken()));
  }

}