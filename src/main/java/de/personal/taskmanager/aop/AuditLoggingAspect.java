package de.personal.taskmanager.aop;

import de.personal.taskmanager.annotation.AuditLog;
import de.personal.taskmanager.model.AuditLogRecord;
import de.personal.taskmanager.respository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Aspect that intercepts methods annotated with @AuditLog to log method execution details.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLoggingAspect {

    private final AuditLogRepository auditLogRepository;

    /**
     * Pointcut for any method annotated with @AuditLog.
     */
    @Pointcut("@annotation(de.personal.taskmanager.annotation.AuditLog)")
    public void auditLogPointcut() {
    }

    /**
     * Advice that logs method entry with audit information.
     */
    @Before("auditLogPointcut() && @annotation(auditLog)")
    public void logAuditInfo(JoinPoint joinPoint, AuditLog auditLog) {
        // Get method signature and arguments
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        String arguments = Arrays.toString(joinPoint.getArgs());

        // Get authenticated user (if present)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";

        // Log structured audit info
        log.info("[AUDIT] {} User: {} | Method: {} | Args: {} | Time: {}",
                auditLog.desc() + " >>> ",
                username,
                methodName,
                arguments,
                LocalDateTime.now());

        // Persist structured audit info into DB
        auditLogRepository.save(
                AuditLogRecord.builder()
                        .username(username)
                        .method(methodName)
                        .arguments(arguments)
                        .description(auditLog.desc())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
