package fit.hutech.NguyenVuThanhNam.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Custom error view resolver - tra ve trang loi tuy chinh
 */
@Component
public class CustomErrorController implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        if (status == HttpStatus.NOT_FOUND) {
            return new ModelAndView("error/404");
        }
        if (status == HttpStatus.FORBIDDEN) {
            return new ModelAndView("error/403");
        }
        return new ModelAndView("error/500");
    }
}

