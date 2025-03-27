package homework_1.mapper;

import homework_1.domain.User;
import homework_1.dto.UserRegistrationDto;
import homework_1.dto.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "default")
public interface UserMapper {
    User toUser(UserRegistrationDto dto);
    @Mapping(source = "role", target = "role")
    UserResponseDto toDto(User user);
}
