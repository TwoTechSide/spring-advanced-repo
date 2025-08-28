package org.example.expert.domain.user.dto.request;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserChangePasswordRequestValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("[UserChangePasswordRequestValidation] Validation 테스트")
    public void userChangePasswordRequestValidation() {

        UserChangePasswordRequest request = new UserChangePasswordRequest("password", "01234ABCDE");

        Set<ConstraintViolation<UserChangePasswordRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
