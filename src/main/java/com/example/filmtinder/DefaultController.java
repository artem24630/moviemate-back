package com.example.filmtinder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import java.net.HttpURLConnection;

public class DefaultController extends AbstractController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "Well, smth went wrong for req = " + request);
        return null;
    }
}
