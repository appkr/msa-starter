package {{packageName}}.application.port.in.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.DateTimeException;
import java.time.LocalDate;

public class NullableLocalDateValidator implements ConstraintValidator<ValidNullableDate, LocalDate> {

  String pattern;

  @Override
  public void initialize(ValidNullableDate constraintAnnotation) {
    this.pattern = constraintAnnotation.pattern();
  }

  @Override
  public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    try {
      LocalDate.from(value);
    } catch (DateTimeException ignored) {
      return false;
    }

    return true;
  }
}
