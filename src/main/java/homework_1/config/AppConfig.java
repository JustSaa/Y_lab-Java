package homework_1.config;

import homework_1.repositories.TransactionRepository;
import homework_1.repositories.jdbc.JdbcTransactionRepository;
import homework_1.services.AnalyticsService;
import homework_1.services.impl.AnalyticsServiceImpl;

import java.sql.Connection;

public class AppConfig {
    private static final Connection connection = ConnectionManager.getConnection();

    private static final TransactionRepository transactionRepository = new JdbcTransactionRepository(connection);
    private static final AnalyticsService analyticsService = new AnalyticsServiceImpl(transactionRepository);

    public static AnalyticsService getAnalyticsService() {
        return analyticsService;
    }
}
