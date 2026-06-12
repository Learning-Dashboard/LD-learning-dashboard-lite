package com.upc.gessi.qrapids.app.presentation.web.mapping;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller("/DetailedQualityFactor")
public class DetailedQualityFactorController {

    @GetMapping("/DetailedQualityFactors/CurrentChart")
    public String CurrentChart(){
        return "DetailedQualityFactors/CurrentChart";
    }

    @GetMapping("/DetailedQualityFactors/CurrentChartRadar")
    public String CurrentChartRadar(){
        return "DetailedQualityFactors/CurrentChart";
    }

    @GetMapping("/DetailedQualityFactors/CurrentChartStacked")
    public String CurrentChartStacked(){
        return "DetailedQualityFactors/CurrentStacked";
    }

    @GetMapping("/DetailedQualityFactors/CurrentChartPolar")
    public String CurrentChartPolar(){
        return "DetailedQualityFactors/CurrentPolar";
    }

    @GetMapping("/DetailedQualityFactors/CurrentTable")
    public String CurrentTable(){
        return "DetailedQualityFactors/CurrentTable";
    }

    @GetMapping("/DetailedQualityFactors/HistoricTable")
    public String HistoricTable(){
        return "DetailedQualityFactors/HistoricTable";
    }

    @GetMapping("/DetailedQualityFactors/HistoricChart")
    public String HistoricChart(){
        return "DetailedQualityFactors/HistoricChart";
    }

}
