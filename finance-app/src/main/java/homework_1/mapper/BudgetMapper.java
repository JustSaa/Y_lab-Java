package homework_1.mapper;

import homework_1.dto.BudgetResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BudgetMapper {
    BudgetResponseDto toBudgetResponseDto(Long userId, double limit);
}
