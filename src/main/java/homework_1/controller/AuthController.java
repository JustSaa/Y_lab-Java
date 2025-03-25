package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.common.exceptions.AuthenticationException;
import homework_1.config.ConnectionManager;
import homework_1.domain.User;
import homework_1.dto.*;
import homework_1.mapper.UserMapper;
import homework_1.repositories.UserRepository;
import homework_1.repositories.jdbc.JdbcUserRepository;
import homework_1.services.AuthService;
import homework_1.services.impl.AuthServiceImpl;

import homework_1.utils.ControllerUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthController extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final UserMapper userMapper;
    private final Validator validator;

    public AuthController(ObjectMapper objectMapper, AuthService authService, UserMapper userMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.authService = authService;
        this.userMapper = userMapper;
        this.validator = validator;
    }

    public AuthController() {
        this.objectMapper = new ObjectMapper();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        this.userMapper = Mappers.getMapper(UserMapper.class);

        try {
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
            case "/register" -> handleRequest(req, resp, UserRegistrationDto.class, this::handleRegister);
            case "/login" -> handleRequest(req, resp, UserLoginDto.class, this::handleLogin);
            case "/block" -> handleRequest(req, resp, AdminActionDto.class, this::handleBlock);
            default -> resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if ("/profile".equals(req.getPathInfo())) {
            handleRequest(req, resp, UserUpdateDto.class, this::handleUpdateProfile);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (req.getPathInfo()) {
            case "/profile" -> handleRequest(req, resp, Map.class, this::handleDeleteSelf);
            case "/user" -> handleRequest(req, resp, AdminActionDto.class, this::handleDeleteByAdmin);
            default -> resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (req.getPathInfo()) {
            case "/users" -> {
                List<UserResponseDto> dtos = authService.getAllUsers().stream().map(userMapper::toDto).toList();
                ControllerUtil.writeSuccess(resp, dtos);
            }
            default -> resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleRegister(HttpServletResponse resp, UserRegistrationDto dto) throws IOException {
        User user = authService.register(dto.getName(), dto.getEmail(), dto.getPassword(), dto.getRole());
        ControllerUtil.writeSuccess(resp, userMapper.toDto(user), HttpServletResponse.SC_CREATED);
    }

    private void handleLogin(HttpServletResponse resp, UserLoginDto dto) throws IOException {
        try {
            User user = authService.login(dto.getEmail(), dto.getPassword());
            ControllerUtil.writeSuccess(resp, userMapper.toDto(user));
        } catch (AuthenticationException e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private void handleUpdateProfile(HttpServletResponse resp, UserUpdateDto dto) throws IOException {
        authService.updateUser(dto.getOldEmail(), dto.getNewName(), dto.getNewEmail(), dto.getNewPassword(), dto.getNewRole());
        ControllerUtil.writeSuccess(resp, Map.of("message", "Профиль обновлён"));
    }

    private void handleDeleteSelf(HttpServletResponse resp, Map<String, String> payload) {
        authService.deleteUser(payload.get("email"));
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void handleDeleteByAdmin(HttpServletResponse resp, AdminActionDto dto) {
        authService.deleteUser(dto.getAdminEmail(), dto.getUserEmail());
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void handleBlock(HttpServletResponse resp, AdminActionDto dto) throws IOException {
        authService.blockUser(dto.getAdminEmail(), dto.getUserEmail());
        ControllerUtil.writeSuccess(resp, Map.of("message", "Пользователь заблокирован"));
    }

    private <T> void handleRequest(HttpServletRequest req, HttpServletResponse resp, Class<T> dtoClass, RequestHandler<T> handler) throws IOException {
        T dto = ControllerUtil.parseRequest(req, resp, objectMapper, validator, dtoClass);
        if (dto != null) {
            try {
                handler.handle(resp, dto);
            } catch (IllegalArgumentException e) {
                ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }

    @FunctionalInterface
    private interface RequestHandler<T> {
        void handle(HttpServletResponse resp, T dto) throws IOException;
    }
}