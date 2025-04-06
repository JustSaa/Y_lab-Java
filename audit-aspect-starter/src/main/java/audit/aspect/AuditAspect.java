package audit.aspect;

import audit.logger.AuditLogger;
import audit.aspect.annotation.Audit;
import audit.aspect.annotation.LogExecutionTime;
import jakarta.annotation.Nullable;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class AuditAspect {

    private final AuditLogger auditLogger;

    @Autowired
    public AuditAspect(@Nullable AuditLogger auditLogger) {
        this.auditLogger = auditLogger != null ? auditLogger : new AuditLogger();
    }

    @Around("@annotation(logTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logTime) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        auditLogger.log("Метод " + joinPoint.getSignature() + " выполнен за " + duration + " мс");
        return result;
    }

    @After("@annotation(audit)")
    public void auditAction(JoinPoint joinPoint, Audit audit) {
        String methodName = joinPoint.getSignature().getName();
        auditLogger.log(audit.action() + " — Метод: " + methodName);
    }
}