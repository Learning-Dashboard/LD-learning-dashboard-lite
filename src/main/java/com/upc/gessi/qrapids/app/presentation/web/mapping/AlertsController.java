package com.upc.gessi.qrapids.app.presentation.web.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/QualityAlerts")
public class AlertsController
{
    @GetMapping("/QualityAlerts")
    public String Alerts(){
        return "Alerts/QualityAlerts";
    }
}
