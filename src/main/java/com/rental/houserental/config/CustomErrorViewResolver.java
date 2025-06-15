package com.rental.houserental.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import static com.rental.houserental.constant.Constant.ErrorMessages.*;
import static com.rental.houserental.constant.Constant.AtrributeNames.*;

import java.util.Map;

@Component
public class CustomErrorViewResolver implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(
            HttpServletRequest request,
            HttpStatus status,
            Map<String, Object> model) {

        return switch (status) {
            case NOT_FOUND -> new ModelAndView("error/404", Map.of(MESSAGE, MSG_404));
            case FORBIDDEN -> new ModelAndView("error/403", Map.of(MESSAGE, MSG_403));
            case INTERNAL_SERVER_ERROR -> new ModelAndView("error/500", Map.of(MESSAGE, MSG_500));
            default -> new ModelAndView("error/generic", Map.of(
                    MESSAGE, MSG_GENERIC,
                    STATUS, status.value()
            ));
        };
    }
}
