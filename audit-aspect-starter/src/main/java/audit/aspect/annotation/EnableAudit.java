package audit.aspect.annotation;

import audit.aspect.config.AuditAspectConfiguration;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AuditAspectConfiguration.class)
public @interface EnableAudit {}