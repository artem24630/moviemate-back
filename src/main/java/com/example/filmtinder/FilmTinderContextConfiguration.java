package com.example.filmtinder;

import com.example.filmtinder.user.interceptor.UserContextInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Import(UserContextHolderConfiguration.class)
public class FilmTinderContextConfiguration implements WebMvcConfigurer {

    private final UserContextInterceptor userContextInterceptor;

    public FilmTinderContextConfiguration(UserContextInterceptor userContextInterceptor) {
        this.userContextInterceptor = userContextInterceptor;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userContextInterceptor);
    }
}
