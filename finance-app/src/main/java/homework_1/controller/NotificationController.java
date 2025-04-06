package homework_1.controller;

import homework_1.dto.NotificationResponseDto;
import homework_1.mapper.NotificationMapper;
import homework_1.repositories.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получение уведомлений", description = "Возвращает список уведомлений по ID пользователя")
    public ResponseEntity<List<NotificationResponseDto>> getNotifications(
            @PathVariable @Min(value = 1, message = "userId должен быть положительным") long userId
    ) {
        List<NotificationResponseDto> notifications = notificationRepository.findByUserId(userId).stream()
                .map(notificationMapper::toDto)
                .toList();
        return ResponseEntity.ok(notifications);
    }
}