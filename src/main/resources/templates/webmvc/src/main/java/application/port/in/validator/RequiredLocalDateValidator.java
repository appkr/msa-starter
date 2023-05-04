package {{packageName}}.application.port.in.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.DateTimeException;
import java.time.LocalDate;

public class RequiredLocalDateValidator implements ConstraintValidator<ValidRequiredDate, LocalDate> {

  String pattern;

  @Override
  public void initialize(ValidRequiredDate constraintAnnotation) {
    this.pattern = constraintAnnotation.pattern();
  }

  @Override
  public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }

    try {
      LocalDate.from(value);
    } catch (DateTimeException ignored) {
      return false;
    }

    return true;
  }
}
