package homework_1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AdminActionDto {
    @Email(message = "Email администратора некорректен")
    @NotBlank(message = "Email администратора обязателен")
    private String adminEmail;

    @Email(message = "Email пользователя некорректен")
    @NotBlank(message = "Email пользователя обязателен")
    private String userEmail;

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}