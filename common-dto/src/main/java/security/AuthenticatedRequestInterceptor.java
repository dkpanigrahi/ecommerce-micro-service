package security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticatedRequestInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<AuthenticatedRequestContext> contextHolder = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // Skip if not a handler method
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod method = (HandlerMethod) handler;

        // Check for @RequestIntercepter annotation
        if (!method.hasMethodAnnotation(RequestIntercepter.class)) {
            return true; // Skip validation
        }
        // Extract headers
        String userUuid = request.getHeader("userUuid");
        String phoneNumber = request.getHeader("phoneNumber");
        String role = request.getHeader("role");
        String isVerifiedUser = request.getHeader("isVerified");

        // Validate headers
        if (userUuid == null || phoneNumber == null || role == null || isVerifiedUser == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required headers");
        }

        Boolean isVerified = Boolean.parseBoolean(isVerifiedUser);

        // Store context
        AuthenticatedRequestContext context = new AuthenticatedRequestContext(userUuid, phoneNumber, role, isVerified);
        contextHolder.set(context);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        contextHolder.remove();
    }

    public static AuthenticatedRequestContext getContext() {
        return contextHolder.get();
    }

}
