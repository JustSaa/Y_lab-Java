package homework_1.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CategoryTest {
    @Test
    void categoryValuesTest() {
        assertThat(Category.values()).containsExactly(
                Category.FOOD,
                Category.HEALTH,
                Category.ENTERTAINMENT,
                Category.TRANSPORT,
                Category.SALARY,
                Category.OTHER
        );
    }
}