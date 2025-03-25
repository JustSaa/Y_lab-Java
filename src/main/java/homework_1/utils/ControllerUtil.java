package homework_1.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.config.JacksonConfig;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

public class ControllerUtil {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final ObjectMapper objectMapper = JacksonConfig.objectMapper();

    /**
     * Валидирует объект DTO и в случае ошибок записывает их в ответ.
     */
    public static <T> boolean validate(T dto, HttpServletResponse resp) throws IOException {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            violations.forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Ошибка валидации", errors);
            return false;
        }
        return true;
    }

    /**
     * Читает JSON-запрос, парсит его в объект DTO и проводит валидацию.
     * В случае ошибки записывает её в HTTP-ответ и возвращает `null`.
     */
    public static <T> T readRequest(HttpServletRequest req, Class<T> dtoClass, HttpServletResponse resp) throws IOException {
        T dto = objectMapper.readValue(req.getInputStream(), dtoClass);
        return validate(dto, resp) ? dto : null;
    }

    /**
     * Записывает JSON-ошибку в HTTP-ответ с заданным статусом.
     */
    public static void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        writeResponse(resp, status, Map.of("errorCode", status, "message", message));
    }

    /**
     * Записывает JSON-ошибку с дополнительными данными.
     */
    public static void writeError(HttpServletResponse resp, int status, String message, Object details) throws IOException {
        writeResponse(resp, status, Map.of("errorCode", status, "message", message, "details", details));
    }

    /**
     * Записывает успешный JSON-ответ с HTTP-статусом 200 (OK).
     */
    public static void writeResponse(HttpServletResponse resp, Object data) throws IOException {
        writeResponse(resp, HttpServletResponse.SC_OK, data);
    }

    /**
     * Записывает успешный JSON-ответ с указанным HTTP-статусом.
     */
    public static void writeResponse(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getOutputStream(), data);
    }
}