package homework_1.mapper;

import homework_1.dto.GoalResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GoalMapper {
    GoalResponseDto toGoalResponseDto(long userId, String name, double targetAmount);
}
