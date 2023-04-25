package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class CheckReleaseDateValidator implements ConstraintValidator<CheckReleaseDate, LocalDate> {

    private LocalDate minimumDate;

    @Override
    public void initialize(CheckReleaseDate constraintAnnotation) {
        minimumDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate == null || !localDate.isBefore(minimumDate);
    }
}
