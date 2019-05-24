package com.atlassian.asap.server;

import com.atlassian.asap.api.Jwt;
import com.atlassian.asap.api.exception.AuthorizationFailedException;
import com.atlassian.asap.core.JwtConstants;
import com.atlassian.asap.core.server.interceptor.Asap;
import com.atlassian.asap.core.server.interceptor.AsapValidator;
import com.atlassian.asap.core.validator.JwtValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class AsapAuthenticationInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private AsapValidator asapValidator;

    @Autowired
    private JwtValidator jwtValidator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            Optional<Asap> asapAnnotation = findAsapAnnotation(hm.getMethod());

            if (asapAnnotation.isPresent()) {
                Asap asap = asapAnnotation.get();
                if (!asap.mandatory()) {
                    return true;
                }

                String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                String serializedJwt = StringUtils.removeStart(authorizationHeader,
                        JwtConstants.HTTP_AUTHORIZATION_HEADER_VALUE_PREFIX);
                if (serializedJwt == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return true;
                }

                Jwt jwt = jwtValidator.readAndValidate(serializedJwt);
                if (jwt == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return true;
                }

                if (isAuthorized(jwt, asap)) {
                    return true;
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return true;
                }
            } else {
                logger.debug(
                        "method {} of class {} is unsecured, allowing request",
                        hm.getMethod().getName(),
                        hm.getMethod().getDeclaringClass().getSimpleName());
                return true;
            }
        } else {
            return true;
        }
    }

    private Optional<Asap> findAsapAnnotation(Method method) {
        return findFirstNonNullAnnotation(
                Asap.class,
                () -> method,
                () -> method.getDeclaringClass(),
                () -> method.getDeclaringClass().getPackage()
        );
    }

    @SafeVarargs
    private static <A extends Annotation> Optional<A> findFirstNonNullAnnotation(Class<A> annotationClass,
                                                                                 Supplier<? extends AnnotatedElement>... annotatedElements) {
        return Stream.of(annotatedElements)
                .map(Supplier::get)
                .filter(e -> e.isAnnotationPresent(annotationClass))
                .map(e -> e.getAnnotation(annotationClass))
                .findFirst();
    }

    private boolean isAuthorized(final Jwt jwt, final Asap asap) {
        try {
            asapValidator.validate(asap, jwt);
            logger.trace("Accepting authorized token with identifier '{}'", jwt.getClaims().getJwtId());
            return true;
        } catch (AuthorizationFailedException e) {
            logger.debug("Authorization failed", e);
            return false;
        }
    }
}
