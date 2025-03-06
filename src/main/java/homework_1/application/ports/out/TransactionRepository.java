package homework_1.application.ports.out;

import homework_1.domain.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс репозитория для работы с транзакциями.
 */
public interface TransactionRepository {

    /**
     * Сохранение новой транзакции.
     * @param transaction транзакция для сохранения
     */
    void save(Transaction transaction);

    /**
     * Получение списка транзакций пользователя по его ID.
     * @param userId идентификатор пользователя
     * @return список транзакций пользователя
     */
    List<Transaction> findByUserId(UUID userId);

    /**
     * Обновление существующей транзакции.
     * @param transaction транзакция для обновления
     */
    void update(Transaction transaction);

    /**
     * Удаление транзакции пользователя по ID.
     * @param userId идентификатор пользователя
     * @param transactionId идентификатор транзакции
     */
    void delete(UUID userId, UUID transactionId);

    /**
     * Поиск транзакции по ID.
     * @param userId идентификатор пользователя
     * @param transactionId идентификатор транзакции
     * @return Optional с транзакцией или пустой, если не найдено
     */
    Optional<Transaction> findById(UUID userId, UUID transactionId);
}