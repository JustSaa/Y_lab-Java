package audit.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditLogger {
    private static final Logger logger = LoggerFactory.getLogger("AuditLogger");

    public void log(String message) {
        logger.info("[AUDIT] " + message);
    }
}
