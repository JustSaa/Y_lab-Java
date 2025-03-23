package homework_1.dto;

import homework_1.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserUpdateDto {

    @NotBlank(message = "Имя обязательно")
    private String newName;

    @Email(message = "Старый email обязателен и должен быть корректным")
    private String oldEmail;

    @Email(message = "Новый email должен быть корректным")
    private String newEmail;

    @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
    private String newPassword;

    @NotNull(message = "Роль обязательна")
    private UserRole newRole;

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getOldEmail() {
        return oldEmail;
    }

    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public UserRole getNewRole() {
        return newRole;
    }

    public void setNewRole(UserRole newRole) {
        this.newRole = newRole;
    }
}