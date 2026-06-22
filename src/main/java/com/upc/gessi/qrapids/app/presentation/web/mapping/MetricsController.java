package com.upc.gessi.qrapids.app.presentation.web.mapping;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller("/Metrics")
public class MetricsController {

    @GetMapping("/Metrics/CurrentChart")
    public String CurrentChart(){
        return "Metrics/CurrentChart";
    }

    @GetMapping("/Metrics/CurrentChartGauge")
    public String CurrentChartGauge(){
        return "Metrics/CurrentChart";
    }

    @GetMapping("/Metrics/CurrentChartSlider")
    public String CurrentSlider(){
        return "Metrics/CurrentSlider";
    }

    @GetMapping("/Metrics/CurrentTable")
    public String CurrentTable(){
        return "Metrics/CurrentTable";
    }

    @GetMapping("/Metrics/HistoricTable")
    public String HistoricTable(){
        return "Metrics/HistoricTable";
    }

    @GetMapping("/Metrics/HistoricChart")
    public String HistoricChart(){
        return "Metrics/HistoricChart";
    }

    @GetMapping("/Metrics/Configuration")
    public String Config(){
        return "Metrics/AdditionalScreens/MetricsConfig";
    }
}
