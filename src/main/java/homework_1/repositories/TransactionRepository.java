package homework_1.repositories;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс репозитория для работы с транзакциями.
 */
public interface TransactionRepository {

    /**
     * Сохранение новой транзакции.
     *
     * @param transaction транзакция для сохранения
     */
    void save(Transaction transaction);

    /**
     * Получение списка транзакций пользователя по его Почте.
     *
     * @param userEmail почта пользователя
     * @return список транзакций пользователя
     */
    List<Transaction> findByUserEmail(String userEmail);

    /**
     * Обновление существующей транзакции.
     *
     * @param transaction транзакция для обновления
     */
    void update(Transaction transaction);

    /**
     * Удаление транзакции пользователя по ID.
     *
     * @param userEmail     почта пользователя
     * @param transactionId идентификатор транзакции
     */
    void delete(String userEmail, UUID transactionId);

    /**
     * Поиск транзакции по ID.
     *
     * @param userEmail     почта пользователя
     * @param transactionId идентификатор транзакции
     * @return Optional с транзакцией или пустой, если не найдено
     */
    Optional<Transaction> findById(String userEmail, UUID transactionId);

    /**
     * Возвращает список транзакций пользователя за указанную дату.
     *
     * @param email почта пользователя
     * @param date  дата транзакции
     * @return список транзакций за указанную дату
     */
    List<Transaction> findByUserEmailAndDate(String email, LocalDate date);

    /**
     * Возвращает список транзакций пользователя по категории.
     *
     * @param email    почта пользователя
     * @param category категория транзакции
     * @return список транзакций указанной категории
     */
    List<Transaction> findByUserEmailAndCategory(String email, Category category);

    /**
     * Возвращает список транзакций пользователя по их типу (доход или расход).
     *
     * @param email почта пользователя
     * @param type  тип транзакции (доход или расход)
     * @return список транзакций указанного типа
     */
    List<Transaction> findByUserEmailAndType(String email, TransactionType type);
}