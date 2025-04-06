package audit.aspect.config;

import audit.aspect.AuditAspect;
import audit.logger.AuditLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditAspectConfiguration {

    @Bean
    public AuditAspect auditAspect(AuditLogger auditLogger) {
        return new AuditAspect(auditLogger);
    }
}