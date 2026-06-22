package com.upc.gessi.qrapids.app.presentation.web.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/Profiles")
public class ProfileController {

    @GetMapping("/Profiles/Configuration")
    public String Profiles(){
        return "Profile/Profiles";
    }

}
