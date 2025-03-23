package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.common.exceptions.AuthenticationException;
import homework_1.config.ConnectionManager;
import homework_1.config.LiquibaseMigrationRunner;
import homework_1.domain.User;
import homework_1.dto.*;
import homework_1.mapper.UserMapper;
import homework_1.repositories.UserRepository;
import homework_1.repositories.jdbc.JdbcUserRepository;
import homework_1.services.AuthService;
import homework_1.services.impl.AuthServiceImpl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.mapstruct.factory.Mappers;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet("/api/auth/*")
public class AuthController extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private AuthService authService;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Override
    public void init() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = ConnectionManager.getConnection();
            UserRepository userRepository = new JdbcUserRepository(connection);
            this.authService = new AuthServiceImpl(userRepository);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации AuthController", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (req.getPathInfo()) {
            case "/register" -> handleRegister(req, resp);
            case "/login" -> handleLogin(req, resp);
            case "/block" -> handleBlock(req, resp);
            default -> resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if ("/profile".equals(req.getPathInfo())) {
            handleUpdateProfile(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (req.getPathInfo()) {
            case "/profile" -> handleDeleteSelf(req, resp);
            case "/user" -> handleDeleteByAdmin(req, resp);
            default -> resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if ("/users".equals(req.getPathInfo())) {
            List<User> users = authService.getAllUsers();
            List<UserResponseDto> dtos = users.stream().map(userMapper::toDto).toList();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), dtos);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserRegistrationDto dto = objectMapper.readValue(req.getInputStream(), UserRegistrationDto.class);
        if (!isValid(dto, resp)) return;

        try {
            User user = authService.register(dto.getName(), dto.getEmail(), dto.getPassword(), dto.getRole());
            UserResponseDto responseDto = userMapper.toDto(user);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getOutputStream(), responseDto);
        } catch (IllegalArgumentException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserLoginDto dto = objectMapper.readValue(req.getInputStream(), UserLoginDto.class);
        if (!isValid(dto, resp)) return;

        try {
            User user = authService.login(dto.getEmail(), dto.getPassword());
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), userMapper.toDto(user));
        } catch (AuthenticationException e) {
            writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private void handleUpdateProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserUpdateDto dto = objectMapper.readValue(req.getInputStream(), UserUpdateDto.class);
        if (!isValid(dto, resp)) return;

        try {
            authService.updateUser(dto.getOldEmail(), dto.getNewName(), dto.getNewEmail(), dto.getNewPassword(), dto.getNewRole());
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "Профиль обновлён"));
        } catch (IllegalArgumentException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleDeleteSelf(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String> payload = objectMapper.readValue(req.getInputStream(), Map.class);
        String email = payload.get("email");
        try {
            authService.deleteUser(email);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (IllegalArgumentException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleDeleteByAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AdminActionDto dto = objectMapper.readValue(req.getInputStream(), AdminActionDto.class);
        if (!isValid(dto, resp)) return;

        try {
            authService.deleteUser(dto.getAdminEmail(), dto.getUserEmail());
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (IllegalArgumentException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleBlock(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AdminActionDto dto = objectMapper.readValue(req.getInputStream(), AdminActionDto.class);
        if (!isValid(dto, resp)) return;

        try {
            authService.blockUser(dto.getAdminEmail(), dto.getUserEmail());
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "Пользователь заблокирован"));
        } catch (IllegalArgumentException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private <T> boolean isValid(T dto, HttpServletResponse resp) throws IOException {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<T> v : violations) {
                errors.put(v.getPropertyPath().toString(), v.getMessage());
            }
            objectMapper.writeValue(resp.getOutputStream(), Map.of("errors", errors));
            return false;
        }
        return true;
    }

    private void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        objectMapper.writeValue(resp.getOutputStream(), Map.of("error", message));
    }
}
