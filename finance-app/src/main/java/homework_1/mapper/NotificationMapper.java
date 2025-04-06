package homework_1.mapper;

import homework_1.domain.Notification;
import homework_1.dto.NotificationResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponseDto toDto(Notification notification);
}
