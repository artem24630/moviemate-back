package com.example.filmtinder.user.interceptor;

import com.example.filmtinder.user.context.UserContext;
import com.example.filmtinder.user.context.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserContextInterceptor implements HandlerInterceptor {

    private final UserContextHolder userContextHolder;

    public UserContextInterceptor(
            UserContextHolder userContextHolder
    ) {
        this.userContextHolder = userContextHolder;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        String header = request.getHeader("X-Device-Token");
        if (StringUtils.isBlank(header)) {
            throw new RuntimeException("Unable to process request without token");
        }
        userContextHolder.setContext(new UserContext(header));

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
