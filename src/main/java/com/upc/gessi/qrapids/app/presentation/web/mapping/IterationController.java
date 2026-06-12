package com.upc.gessi.qrapids.app.presentation.web.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/Iterations")
public class IterationController {
    @GetMapping("/Iterations/Configuration")
    public String Profiles(){
        return "Iterations/Iterations";
    }
}
