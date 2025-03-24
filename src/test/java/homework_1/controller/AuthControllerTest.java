package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.domain.User;
import homework_1.domain.UserRole;
import homework_1.dto.UserLoginDto;
import homework_1.dto.UserRegistrationDto;
import homework_1.dto.UserResponseDto;
import homework_1.mapper.UserMapper;
import homework_1.services.AuthService;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    private AuthController controller;
    private AuthService authService;
    private UserMapper userMapper;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ByteArrayOutputStream outputStream;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws IOException {
        objectMapper = new ObjectMapper();
        authService = mock(AuthService.class);
        userMapper = mock(UserMapper.class);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        controller = new AuthController(objectMapper, authService, userMapper, validator);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        outputStream = new ByteArrayOutputStream();

        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() { return true; }
            @Override
            public void setWriteListener(jakarta.servlet.WriteListener writeListener) {}
            @Override
            public void write(int b) {
                outputStream.write(b);
            }
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);
    }

    @Test
    void testRegister_Success() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto("Alice", "alice@example.com", "secret", UserRole.USER);
        User user = new User("Alice", "alice@example.com", "secret", UserRole.USER);
        UserResponseDto responseDto = new UserResponseDto("Alice", "alice@example.com", UserRole.USER);

        when(request.getPathInfo()).thenReturn("/register");
        when(request.getInputStream()).thenReturn(toServletInputStream(dto));
        when(authService.register(any(), any(), any(), any())).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        String result = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("alice@example.com"));
    }

    @Test
    void testLogin_Success() throws Exception {
        UserLoginDto dto = new UserLoginDto("bob@example.com", "pass");
        User user = new User("Bob", "bob@example.com", "pass", UserRole.USER);
        UserResponseDto responseDto = new UserResponseDto("Bob", "bob@example.com", UserRole.USER);

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getInputStream()).thenReturn(toServletInputStream(dto));
        when(authService.login(any(), any())).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String result = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("bob@example.com"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(request.getPathInfo()).thenReturn("/users");
        List<User> users = List.of(new User("Admin", "admin@example.com", "admin", UserRole.ADMIN));
        List<UserResponseDto> dtos = List.of(new UserResponseDto("Admin", "admin@example.com", UserRole.ADMIN));

        when(authService.getAllUsers()).thenReturn(users);
        when(userMapper.toDto(users.get(0))).thenReturn(dtos.get(0));

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String result = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("admin@example.com"));
    }

    @Test
    void testRegister_InvalidData() throws Exception {
        // Пустое имя, email, пароль - невалидные данные
        UserRegistrationDto dto = new UserRegistrationDto("", "", "", null);

        when(request.getPathInfo()).thenReturn("/register");
        when(request.getInputStream()).thenReturn(toServletInputStream(dto));

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String result = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("email") || result.contains("password") || result.contains("validation"), "Ошибка валидации должна присутствовать");
    }

    @Test
    void testUnknownPath() throws Exception {
        when(request.getPathInfo()).thenReturn("/unknown");

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        String result = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(result.isEmpty(), "Ответ должен быть пустым для неизвестного пути");
    }

    private ServletInputStream toServletInputStream(Object dto) throws IOException {
        String json = objectMapper.writeValueAsString(dto);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        return new ServletInputStream() {
            @Override public int read() { return byteStream.read(); }
            @Override public boolean isFinished() { return byteStream.available() == 0; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(jakarta.servlet.ReadListener readListener) {}
        };
    }
}