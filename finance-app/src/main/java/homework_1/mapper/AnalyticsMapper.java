package homework_1.mapper;

import homework_1.dto.CategoryResponseDto;
import homework_1.dto.ExpensesResponseDto;
import homework_1.dto.FullReportResponseDto;
import homework_1.dto.IncomeResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnalyticsMapper {
    IncomeResponseDto toIncomeDto(Double income);

    ExpensesResponseDto toExpensesDto(Double expenses);

    CategoryResponseDto toCategoryDto(String report);
    @Mapping(source = "report", target = "fullReport")
    FullReportResponseDto toFullReportDto(String report);
}
