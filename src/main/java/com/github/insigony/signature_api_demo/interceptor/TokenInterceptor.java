package com.github.insigony.signature_api_demo.interceptor;

import com.github.insigony.signature_api_demo.config.AuthProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    private final AuthProperties authProperties;

    public TokenInterceptor(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String token = request.getHeader("Token");
        if (token == null || !token.equals(authProperties.getToken())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden");
            return false;
        }
        return true;
    }
}