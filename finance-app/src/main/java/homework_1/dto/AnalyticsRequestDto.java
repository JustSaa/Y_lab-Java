package homework_1.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class AnalyticsRequestDto {

    @NotNull(message = "userId обязателен")
    @Min(value = 1, message = "userId должен быть больше 0")
    private Long userId;

    @NotNull(message = "Дата начала обязательна")
    @PastOrPresent(message = "Дата начала не может быть в будущем")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate start;

    @NotNull(message = "Дата окончания обязательна")
    @PastOrPresent(message = "Дата окончания не может быть в будущем")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate end;

    public AnalyticsRequestDto() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    @AssertTrue(message = "Дата начала должна быть раньше даты окончания")
    public boolean isValidPeriod() {
        return !start.isAfter(end);
    }
}