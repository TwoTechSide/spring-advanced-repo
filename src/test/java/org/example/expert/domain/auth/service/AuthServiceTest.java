package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("[AuthService] signup 메서드 이메일 중복 오류 테스트")
    public void signup_이메일_중복_오류() {

        SignupRequest signupRequest = new SignupRequest("test@gmail.com", "password", "USER");

        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(true);

        assertThatThrownBy(() -> authService.signup(signupRequest)).isInstanceOf(InvalidRequestException.class);
    }

    @Test
    @DisplayName("[AuthService] signup 메서드 테스트")
    public void signup() {

        SignupRequest signupRequest = new SignupRequest("test@gmail.com", "password", "USER");
        User savedUser = new User("test@gamil.com", "encodedPassword", UserRole.USER);

        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(false);
        given(passwordEncoder.encode(signupRequest.getPassword())).willReturn(signupRequest.getPassword());
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), UserRole.USER)).willReturn("token");

        SignupResponse result = authService.signup(signupRequest);

        assertThat(result).isNotNull();
    }
}
