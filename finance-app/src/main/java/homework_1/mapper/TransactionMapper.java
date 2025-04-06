package homework_1.mapper;

import homework_1.domain.Transaction;
import homework_1.dto.TransactionRequestDto;
import homework_1.dto.TransactionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    Transaction toEntity(TransactionRequestDto dto);
    TransactionResponseDto toDto(Transaction transaction);
}