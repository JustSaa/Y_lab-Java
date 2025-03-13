package homework_1.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TransactionTypeTest {
    @Test
    void transactionTypeEnum_HasCorrectValues() {
        assertThat(TransactionType.values())
                .containsExactly(TransactionType.INCOME, TransactionType.EXPENSE);
    }
}