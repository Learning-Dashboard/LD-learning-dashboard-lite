package com.upc.gessi.qrapids.app.presentation.web.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/QualityModel")
public class QualityModelController {
    @GetMapping("/QualityModelGraph")
    public String CurrentChartGraph(){
        return "QualityModel/QualityModelGraph";
    }
    @GetMapping("/QualityModelSunburst")
    public String CurrentChartSunburst(){
        return "QualityModel/QualityModelSunburst";
    }
}
