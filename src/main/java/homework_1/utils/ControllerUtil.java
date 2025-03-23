package homework_1.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

public class ControllerUtil {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> boolean validate(T dto, HttpServletResponse resp) throws IOException {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<T> v : violations) {
                errors.put(v.getPropertyPath().toString(), v.getMessage());
            }
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, errors);
            return false;
        }
        return true;
    }

    public static void writeError(HttpServletResponse resp, int status, Object message) throws IOException {
        resp.setStatus(status);
        objectMapper.writeValue(resp.getOutputStream(), Map.of("error", message));
    }
}