package homework_1.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class TransactionTest {

    @Test
    void gettersAndSetters_WorkCorrectly() {
        long userId = 0;
        Transaction transaction = new Transaction(
                1, userId, 500, TransactionType.EXPENSE, Category.FOOD,
                LocalDate.of(2024, 3, 6), "Покупка продуктов");

        transaction.setAmount(600);
        transaction.setType(TransactionType.INCOME);
        transaction.setCategory(Category.SALARY);
        transaction.setDate(LocalDate.of(2024, 3, 10));
        transaction.setDescription("Зарплата за март");

        assertThat(transaction.getUserId()).isEqualTo(userId);
        assertThat(transaction.getAmount()).isEqualTo(600);
        assertThat(transaction.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(transaction.getCategory()).isEqualTo(Category.SALARY);
        assertThat(transaction.getDate()).isEqualTo(LocalDate.of(2024, 3, 10));
        assertThat(transaction.getDescription()).isEqualTo("Зарплата за март");
    }

    @Test
    void testEqualsAndHashCode() {
        long userId = 0;

        Transaction transaction1 = new Transaction(
                1, userId, 1000, TransactionType.INCOME, Category.SALARY,
                LocalDate.now(), "Зарплата");

        assertThat(transaction1).isEqualTo(transaction1);
        assertThat(transaction1.hashCode()).isEqualTo(transaction1.hashCode());
    }
}