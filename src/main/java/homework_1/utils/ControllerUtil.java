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

    public static <T> T parseRequest(HttpServletRequest req, HttpServletResponse resp, ObjectMapper objectMapper, Validator validator, Class<T> dtoClass) throws IOException {
        T dto = objectMapper.readValue(req.getInputStream(), dtoClass);
        Set<ConstraintViolation<T>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Ошибка валидации");
            return null;
        }
        return dto;
    }

    public static void writeSuccess(HttpServletResponse resp, Object data) throws IOException {
        writeSuccess(resp, data, HttpServletResponse.SC_OK);
    }

    public static void writeSuccess(HttpServletResponse resp, Object data, int status) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        new ObjectMapper().writeValue(resp.getOutputStream(), data);
    }

}