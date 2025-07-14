package org.projects.dupligonebackend.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidImageValidator.class)
public @interface ValidImage {
    String message() default "Invalid image file type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
