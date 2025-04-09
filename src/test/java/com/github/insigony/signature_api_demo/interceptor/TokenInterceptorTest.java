package com.github.insigony.signature_api_demo.interceptor;

import com.github.insigony.signature_api_demo.config.AuthProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenInterceptorTest {

    @InjectMocks
    private TokenInterceptor tokenInterceptor;

    @Mock
    private AuthProperties authProperties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    @Mock
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        when(authProperties.getToken()).thenReturn("super-secret-token");

        lenient().when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void shouldAllowRequestWithValidToken() throws Exception {
        when(request.getHeader("Token")).thenReturn("super-secret-token");
        boolean result = tokenInterceptor.preHandle(request, response, handler);

        assertTrue(result);
    }

    @Test
    void shouldRejectRequestWithInvalidToken() throws Exception {
        when(request.getHeader("Token")).thenReturn("invalid-token");
        boolean result = tokenInterceptor.preHandle(request, response, handler);

        assertFalse(result);

        verify(response).getWriter();
        verify(printWriter).write("Forbidden");
    }
}
