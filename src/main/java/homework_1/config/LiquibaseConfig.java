package homework_1.config;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    private static final Logger log = LoggerFactory.getLogger(LiquibaseConfig.class);

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource,
                                     @Value("${db.changelog.file}") String changelogFile) {
        log.info("Инициализация Liquibase с changelog: {}", changelogFile);

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changelogFile);
        liquibase.setShouldRun(true);
        liquibase.setContexts("development");

        log.info("Liquibase настроен и готов к запуску миграций");

        return liquibase;
    }
}