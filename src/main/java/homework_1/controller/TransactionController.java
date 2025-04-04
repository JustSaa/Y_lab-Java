package homework_1.controller;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.dto.TransactionRequestDto;
import homework_1.dto.TransactionResponseDto;
import homework_1.mapper.TransactionMapper;
import homework_1.services.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(@Valid @RequestBody TransactionRequestDto dto) {
        Transaction transaction = mapper.toEntity(dto);
        transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(transaction));
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateTransaction(@Valid @RequestBody TransactionRequestDto dto) {
        Transaction transaction = mapper.toEntity(dto);
        transactionService.updateTransaction(transaction);
        return ResponseEntity.ok(Map.of("message", "Транзакция обновлена"));
    }

    @DeleteMapping("/{userId}/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable @Min(1) long userId,
                                                  @PathVariable @Min(1) long transactionId) {
        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(
            @PathVariable @Min(1) long userId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) LocalDate date
    ) {
        List<Transaction> transactions;

        if (type != null) {
            transactions = transactionService.getTransactionsByType(userId, type);
        } else if (category != null) {
            transactions = transactionService.getTransactionsByCategory(userId, category);
        } else if (date != null) {
            transactions = transactionService.getTransactionsByDate(userId, date);
        } else {
            transactions = transactionService.getTransactions(userId);
        }

        List<TransactionResponseDto> dtos = transactions.stream()
                .map(mapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}