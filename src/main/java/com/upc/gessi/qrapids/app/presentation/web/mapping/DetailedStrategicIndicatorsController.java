package com.upc.gessi.qrapids.app.presentation.web.mapping;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller("/DetailedStrategicIndicators")
public class DetailedStrategicIndicatorsController {

    @GetMapping("/DetailedStrategicIndicators/CurrentChart")
    public String CurrentChart(){
        return "DetailedStrategicIndicators/CurrentChart";
    }

    @GetMapping("/DetailedStrategicIndicators/CurrentChartRadar")
    public String CurrentChartRadar(){
        return "DetailedStrategicIndicators/CurrentChart";
    }

    @GetMapping("/DetailedStrategicIndicators/CurrentChartStacked")
    public String CurrentChartStacked(){
        return "DetailedStrategicIndicators/CurrentStacked";
    }

    @GetMapping("/DetailedStrategicIndicators/CurrentChartPolar")
    public String CurrentChartPolar(){
        return "DetailedStrategicIndicators/CurrentPolar";
    }

    @GetMapping("/DetailedStrategicIndicators/CurrentChartBar")
    public String CurrentChartBar(){
        return "DetailedStrategicIndicators/CurrentBar";
    }

    @GetMapping("/DetailedStrategicIndicators/CurrentTable")
    public String CurrentTable(){
        return "DetailedStrategicIndicators/CurrentTable";
    }

    @GetMapping("/DetailedStrategicIndicators/HistoricTable")
    public String HistoricTable(){
        return "DetailedStrategicIndicators/HistoricTable";
    }

    @GetMapping("/DetailedStrategicIndicators/HistoricChart")
    public String HistoricChart(){
        return "DetailedStrategicIndicators/HistoricChart";
    }

}