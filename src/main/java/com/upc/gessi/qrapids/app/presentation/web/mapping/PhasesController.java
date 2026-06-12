package com.upc.gessi.qrapids.app.presentation.web.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/Phases")
public class PhasesController {
    @GetMapping("/Phases")
    public String CurrentChart(){
        return "Phases/Phases";
    }
}
