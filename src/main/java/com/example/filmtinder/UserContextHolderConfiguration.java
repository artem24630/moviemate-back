package com.example.filmtinder;

import com.example.filmtinder.user.context.UserContextHolder;
import com.example.filmtinder.user.interceptor.UserContextInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class UserContextHolderConfiguration {
    @Bean
    @RequestScope
    public UserContextHolder userContextHolder() {
        return new UserContextHolder();
    }

    @Bean
    public UserContextInterceptor userContextInterceptor(UserContextHolder userContextHolder) {
        return new UserContextInterceptor(userContextHolder);
    }
}
