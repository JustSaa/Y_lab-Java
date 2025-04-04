package homework_1.controller;

import homework_1.domain.User;
import homework_1.dto.*;
import homework_1.exceptions.AuthenticationException;
import homework_1.mapper.UserMapper;
import homework_1.services.AuthService;
import jakarta.validation.Valid;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegistrationDto dto) {
        User user = authService.register(dto.getName(), dto.getEmail(), dto.getPassword(), dto.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto dto) {
        try {
            User user = authService.login(dto.getEmail(), dto.getPassword());
            return ResponseEntity.ok(userMapper.toDto(user));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/block")
    public ResponseEntity<Map<String, String>> blockUser(@Valid @RequestBody AdminActionDto dto) {
        authService.blockUser(dto.getAdminEmail(), dto.getUserEmail());
        return ResponseEntity.ok(Map.of("message", "Пользователь заблокирован"));
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(@Valid @RequestBody UserUpdateDto dto) {
        authService.updateUser(dto.getOldEmail(), dto.getNewName(), dto.getNewEmail(), dto.getNewPassword(), dto.getNewRole());
        return ResponseEntity.ok(Map.of("message", "Профиль обновлён"));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteSelf(@RequestBody Map<String, String> payload) {
        authService.deleteUser(payload.get("email"));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user")
    public ResponseEntity<Void> deleteByAdmin(@Valid @RequestBody AdminActionDto dto) {
        authService.deleteUser(dto.getAdminEmail(), dto.getUserEmail());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> dtos = authService.getAllUsers().stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }
}