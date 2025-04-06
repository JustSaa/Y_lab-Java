package homework_1.controller;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.dto.ApiResponse;
import homework_1.dto.TransactionRequestDto;
import homework_1.dto.TransactionResponseDto;
import homework_1.mapper.TransactionMapper;
import homework_1.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    public TransactionController(TransactionService transactionService, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    @PostMapping
    @Operation(summary = "Создание транзакции", description = "Создаёт новую транзакцию (расход или доход)")
    public ResponseEntity<TransactionResponseDto> createTransaction(@Valid @RequestBody TransactionRequestDto dto) {
        Transaction transaction = transactionMapper.toEntity(dto);
        transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionMapper.toDto(transaction));
    }

    @PutMapping
    @Operation(summary = "Обновление транзакции", description = "Обновляет существующую транзакцию")
    public ResponseEntity<ApiResponse<Void>> updateTransaction(@Valid @RequestBody TransactionRequestDto dto) {
        Transaction transaction = transactionMapper.toEntity(dto);
        transactionService.updateTransaction(transaction);
        return ResponseEntity.ok(new ApiResponse<>("Транзакция обновлена", null));
    }

    @DeleteMapping("/{userId}/{transactionId}")
    @Operation(summary = "Удаление транзакции", description = "Удаляет транзакцию по ID пользователя и транзакции")
    public ResponseEntity<Void> deleteTransaction(@PathVariable @Min(1) long userId,
                                                  @PathVariable @Min(1) long transactionId) {
        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получение транзакций", description = "Получает список транзакций с возможностью фильтрации по типу, категории и дате")
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(
            @PathVariable @Min(1) long userId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) LocalDate date
    ) {
        List<Transaction> transactions = transactionService.getFilteredTransactions(userId, type, category, date);

        List<TransactionResponseDto> dtos = transactions.stream()
                .map(transactionMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}