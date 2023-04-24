package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckReleaseDateValidator.class)
public @interface CheckReleaseDate {
    String value() default "1895-12-28";

    String message() default "Дата релиза должна быть позднее чем 28 декабря 1895";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
