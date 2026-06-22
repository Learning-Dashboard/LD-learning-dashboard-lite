package com.upc.gessi.qrapids.app.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OpenApiRedirectController {

    @GetMapping({"/swagger-ui", "/swagger-ui/"})
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui.html";
    }
}
