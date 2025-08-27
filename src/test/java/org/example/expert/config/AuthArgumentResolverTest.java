package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class AuthArgumentResolverTest {

    @InjectMocks
    private AuthUserArgumentResolver authUserArgumentResolver;

    @Mock
    private MethodParameter parameter;

    @Test
    @DisplayName("[AuthArgumentResolver] supportsParameter 메서드 테스트")
    public void supportsParameter() {

        // given
        given(parameter.getParameterAnnotation(Auth.class)).willReturn(mock(Auth.class));
        given(parameter.getParameterType()).willReturn((Class) AuthUser.class);

        // when
        boolean result = authUserArgumentResolver.supportsParameter(parameter);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("[AuthArgumentResolver] @Auth 어노테이션 적용 테스트")
    public void resolverArgumentTest() {

        Long userId = 1L;
        String email = "test@gmail.com";
        String userRole = "USER";

        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getAttribute("userId")).willReturn(userId);
        given(request.getAttribute("email")).willReturn(email);
        given(request.getAttribute("userRole")).willReturn(userRole);

        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        given(webRequest.getNativeRequest()).willReturn(request);

        // when
        Object result = authUserArgumentResolver.resolveArgument(null, null, webRequest, null);

        // then
        assertThat(result).isInstanceOf(AuthUser.class);
        AuthUser authUser = (AuthUser) result;
        assertThat(authUser.getId()).isEqualTo(userId);
        assertThat(authUser.getEmail()).isEqualTo(email);
        assertThat(authUser.getUserRole()).isEqualTo(UserRole.USER);
    }
}
