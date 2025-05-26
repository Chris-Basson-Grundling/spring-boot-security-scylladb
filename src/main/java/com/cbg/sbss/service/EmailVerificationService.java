package com.cbg.sbss.service;

import static com.cbg.sbss.exception.ErrorType.EMAIL_ALREADY_VERIFIED;
import static com.cbg.sbss.exception.ErrorType.EMAIL_VERIFICATION_FAILED;
import static com.cbg.sbss.exception.ProblemDetailBuilder.forStatusAndDetail;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.cbg.sbss.dto.UserDto;
import com.cbg.sbss.exception.RestErrorResponseException;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

  private final OtpService otpService;

  private final JavaMailSender mailSender;

  private final UserService userService;

  @Async
  public void sendEmailVerificationOtp(final UUID userId, final String email) {
    final var token = otpService.generateAndStoreOtp(userId);
    final var emailText = "Enter the following email verification code: " + token;

    final var message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("Email Verification");
    message.setFrom("System");
    message.setText(emailText);

    mailSender.send(message);
  }

  public void resendEmailVerificationOtp(final String email) {
    userService.findByEmail(email).filter(user -> !user.isEmailVerified())
        .ifPresentOrElse(user -> sendEmailVerificationOtp(user.getId(), user.getEmail()),
            () -> log.warn(
                "Attempt to resend verification token for non existing or already validated email: [{}]",
                email));
  }

  @Transactional
  public UserDto verifyEmailOtp(final String email, final String otp) {
    final var user = userService.findByEmail(email).orElseThrow(
        () -> new RestErrorResponseException(
            forStatusAndDetail(BAD_REQUEST, "Invalid email or token").withErrorType(
                EMAIL_VERIFICATION_FAILED).build()));

    if (!otpService.isOtpValid(user.getId(), otp)) {
      throw new RestErrorResponseException(
          forStatusAndDetail(BAD_REQUEST, "Invalid email or token").withErrorType(
              EMAIL_VERIFICATION_FAILED).build());
    }
    otpService.deleteOtp(user.getId());

    if (user.isEmailVerified()) {
      throw new RestErrorResponseException(
          forStatusAndDetail(BAD_REQUEST, "Email is already verified").withErrorType(
              EMAIL_ALREADY_VERIFIED).build());
    }

    userService.verifyEmail(user.getId());

    return user;
  }

}