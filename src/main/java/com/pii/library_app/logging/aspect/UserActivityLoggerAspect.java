package com.pii.library_app.logging.aspect;

import com.pii.library_app.logging.model.UserActivityLog;
import com.pii.library_app.logging.repo.UserActivityLogRepository;
import com.pii.library_app.user.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class UserActivityLoggerAspect {

    private final UserActivityLogRepository logRepository;
    private final UserService userService;

    public UserActivityLoggerAspect(
            UserActivityLogRepository logRepository,
            UserService userService
    ) {
        this.logRepository = logRepository;
        this.userService = userService;
    }

    @Pointcut("execution(* com.pii.library_app..controller..*(..))")
    public void allControllerMethods() {
    }

    @Around("allControllerMethods()")
    public Object logUserActivity(ProceedingJoinPoint joinPoint) throws Throwable {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated();
        String username;
        Long userId;
        if (isAuthenticated) {
            username = auth.getName();
            if (!Objects.equals("anonymousUser", username)) {
                userId = userService.findByUsername(username).getId();
            } else {
                userId = -1L;
            }
        } else {
            userId = -1L;
        }

        var methodName = joinPoint.getSignature().getName();
        var className = joinPoint.getTarget().getClass().getSimpleName();

        var logEntry = new UserActivityLog(userId, methodName, className);
        logRepository.save(logEntry);

        return joinPoint.proceed();
    }
}
