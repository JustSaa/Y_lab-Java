package homework_1.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(logTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logTime) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();  // вызов метода
        long duration = System.currentTimeMillis() - start;
        logger.info("Метод {} выполнен за {} мс", joinPoint.getSignature(), duration);
        return result;
    }

    @After("@annotation(audit)")
    public void auditAction(Audit audit) {
        logger.info("Аудит: {}", audit.action());
    }
}