package homework_1.ui;

import homework_1.domain.*;
import homework_1.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class TransactionConsoleHandlerTest {

    private TransactionService transactionService;
    private TransactionConsoleHandler consoleHandler;
    private Scanner scanner;
    private User user;

    @BeforeEach
    void setUp() {
        transactionService = Mockito.mock(TransactionService.class);
        scanner = new Scanner("");
        consoleHandler = new TransactionConsoleHandler(transactionService, scanner);
        user = new User("Тест", "test@mail.com", "password");
    }

    @Test
    void createTransaction_Success() {
        scanner = new Scanner("1000\n1\nSALARY\nЗарплата\n");
        consoleHandler = new TransactionConsoleHandler(transactionService, scanner);

        consoleHandler.createTransaction(user);

        verify(transactionService, times(1)).createTransaction(any(Transaction.class));
    }

    @Test
    void createTransaction_InvalidAmount_ShouldNotCreateTransaction() {
        scanner = new Scanner("-500\n1\nSALARY\nЗарплата\n");
        consoleHandler = new TransactionConsoleHandler(transactionService, scanner);

        consoleHandler.createTransaction(user);

        verify(transactionService, never()).createTransaction(any(Transaction.class));
    }

    @Test
    void createTransaction_InvalidTransactionType_ShouldNotCreateTransaction() {
        scanner = new Scanner("500\n3\nSALARY\nЗарплата\n");
        consoleHandler = new TransactionConsoleHandler(transactionService, scanner);

        consoleHandler.createTransaction(user);

        verify(transactionService, never()).createTransaction(any(Transaction.class));
    }

    @Test
    void createTransaction_InvalidCategory_ShouldNotCreateTransaction() {
        scanner = new Scanner("500\n1\nUNKNOWN\nЗарплата\n");
        consoleHandler = new TransactionConsoleHandler(transactionService, scanner);

        consoleHandler.createTransaction(user);

        verify(transactionService, never()).createTransaction(any(Transaction.class));
    }

    @Test
    void showTransactions_ShouldPrintTransactions() {
        List<Transaction> transactions = List.of(
                new Transaction(user.getEmail(), 1000, TransactionType.INCOME, Category.SALARY, LocalDate.now(), "Зарплата"),
                new Transaction(user.getEmail(), 500, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Обед")
        );

        when(transactionService.getTransactions(user.getEmail())).thenReturn(transactions);

        consoleHandler.showTransactions(user);

        verify(transactionService, times(1)).getTransactions(user.getEmail());
    }

    @Test
    void showBalance_ShouldPrintCorrectBalance() {
        when(transactionService.calculateBalance(user.getEmail())).thenReturn(1500.0);

        consoleHandler.showBalance(user);

        verify(transactionService, times(1)).calculateBalance(user.getEmail());
    }
}