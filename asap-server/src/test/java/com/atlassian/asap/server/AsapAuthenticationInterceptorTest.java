package com.atlassian.asap.server;

import com.atlassian.asap.api.Jwt;
import com.atlassian.asap.api.JwtClaims;
import com.atlassian.asap.api.exception.AuthorizationFailedException;
import com.atlassian.asap.core.server.interceptor.Asap;
import com.atlassian.asap.core.server.interceptor.AsapValidator;
import com.atlassian.asap.core.validator.JwtValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AsapAuthenticationInterceptorTest {

    private static final String TOKEN = "token";
    @Mock
    private AsapValidator asapValidator;
    @Mock
    private JwtValidator jwtValidator;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @InjectMocks
    private AsapAuthenticationInterceptor asapAuthenticationInterceptor;
    private HandlerMethod securedMethod;
    private HandlerMethod unsecuredMethod;
    private HandlerMethod nonMandatoryAsap;

    @BeforeEach
    public void setup() throws NoSuchMethodException {
        TestController controller = new TestController();
        unsecuredMethod = new HandlerMethod(controller, controller.getClass().getMethod("unsecuredMethod"));
        securedMethod = new HandlerMethod(controller, controller.getClass().getMethod("securedMethod"));
        nonMandatoryAsap = new HandlerMethod(controller, controller.getClass().getMethod("nonMandatoryAsap"));
    }

    @Test
    public void shouldAllowAccessToMethodWhenNotAnnotatedWithAsap() throws Exception {
        boolean result = asapAuthenticationInterceptor.preHandle(request, response, unsecuredMethod);
        assertThat(result, is(true));
    }

    @Test
    public void shouldAllowAccessToMethodWhenAnnotatedWithAsapThatIsNotMandatory() throws Exception {
        boolean result = asapAuthenticationInterceptor.preHandle(request, response, nonMandatoryAsap);
        assertThat(result, is(true));
    }

    public void shouldNotAllowAccessToMethodAndReturn403WhenAsapIssuerIsInvalid() throws Exception {
        Jwt jwt = mock(Jwt.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(TOKEN);
        when(jwtValidator.readAndValidate(TOKEN)).thenReturn(jwt);

        Mockito.doThrow(new AuthorizationFailedException("get out")).when(asapValidator).validate(any(), any());

        boolean result = asapAuthenticationInterceptor.preHandle(request, response, securedMethod);

        assertTrue(result);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));
    }

    @Test
    public void shouldAuthenticateAndAuthorizeRequest() throws Exception {
        Jwt jwt = mock(Jwt.class);
        JwtClaims jwtClaims = mock(JwtClaims.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(TOKEN);
        when(jwtValidator.readAndValidate(TOKEN)).thenReturn(jwt);
        when(jwt.getClaims()).thenReturn(jwtClaims);

        boolean result = asapAuthenticationInterceptor.preHandle(request, response, securedMethod);
        assertThat(result, is(true));

        verify(asapValidator).validate(securedMethod.getMethodAnnotation(Asap.class), jwt);
    }


    private static class TestController {
        public void unsecuredMethod() {
        }

        @Asap
        public void securedMethod() {
        }

        @Asap(mandatory = false)
        public void nonMandatoryAsap() {
        }
    }
}