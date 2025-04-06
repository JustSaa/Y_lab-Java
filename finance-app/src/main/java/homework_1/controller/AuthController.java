package homework_1.controller;

import homework_1.domain.User;
import homework_1.dto.*;
import homework_1.mapper.UserMapper;
import homework_1.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegistrationDto dto) {
        User user = authService.register(dto.getName(), dto.getEmail(), dto.getPassword(), dto.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(user));
    }

    @PostMapping("/login")
    @Operation(summary = "Авторизация пользователя")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto dto) {
        User user = authService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping("/block")
    @Operation(summary = "Блокировка пользователя")
    public ResponseEntity<ApiResponse<Void>> blockUser(@Valid @RequestBody AdminActionDto dto) {
        authService.blockUser(dto.getAdminEmail(), dto.getUserEmail());
        return ResponseEntity.ok(new ApiResponse<>("Пользователь заблокирован", null));
    }

    @PutMapping("/profile")
    @Operation(summary = "Обновление профиля пользователя")
    public ResponseEntity<ApiResponse<Void>> updateProfile(@Valid @RequestBody UserUpdateDto dto) {
        authService.updateUser(dto.getOldEmail(), dto.getNewName(), dto.getNewEmail(), dto.getNewPassword(), dto.getNewRole());
        return ResponseEntity.ok(new ApiResponse<>("Профиль обновлён", null));
    }

    @DeleteMapping("/profile")
    @Operation(summary = "Удаление своего профиля")
    public ResponseEntity<Void> deleteSelf(@RequestBody Map<String, String> payload) {
        authService.deleteUser(payload.get("email"));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user")
    @Operation(summary = "Удаление пользователя админом")
    public ResponseEntity<Void> deleteByAdmin(@Valid @RequestBody AdminActionDto dto) {
        authService.deleteUser(dto.getAdminEmail(), dto.getUserEmail());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    @Operation(summary = "Получение списка пользователей")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> dtos = authService.getAllUsers().stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }
}