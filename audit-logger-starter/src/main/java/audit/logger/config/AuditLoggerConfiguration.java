package audit.logger.config;

import audit.logger.AuditLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditLoggerConfiguration {

    @Bean
    public AuditLogger auditLogger() {
        return new AuditLogger();
    }
}