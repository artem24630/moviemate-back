package com.example.filmtinder.user.context;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

public class UserContextHolder {

    private UserContext context;

    public void setContext(UserContext context) {
        this.context = context;
    }

    public UserContext getContext() {
        return context;
    }
}
