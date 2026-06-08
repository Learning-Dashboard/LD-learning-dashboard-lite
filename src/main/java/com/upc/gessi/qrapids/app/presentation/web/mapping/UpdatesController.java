package com.upc.gessi.qrapids.app.presentation.web.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/Updates")
public class UpdatesController {

    @GetMapping("/Updates/Configuration")
    public String Updates(){ return "Updates/Updates";}
}
