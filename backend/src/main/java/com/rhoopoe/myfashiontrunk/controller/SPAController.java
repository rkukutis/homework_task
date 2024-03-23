package com.rhoopoe.myfashiontrunk.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SPAController implements ErrorController {

    // when we go to an unknown path (such as a react router route) we get redirected to an error page
    // here we redirect from an error page to index.html
    @RequestMapping("/error")
    public Object error(HttpServletRequest request, HttpServletResponse response) {
        if (request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
            response.setStatus(HttpStatus.OK.value());
            return "forward:/index.html";
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public String getErrorPath() {
        return "/error";
    }
}
