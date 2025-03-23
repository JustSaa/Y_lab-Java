package homework_1.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

public class ValidationUtil {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> boolean validate(T dto, HttpServletResponse resp) throws IOException {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<T> v : violations) {
                errors.put(v.getPropertyPath().toString(), v.getMessage());
            }
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("errors", errors));
            return false;
        }
        return true;
    }
}