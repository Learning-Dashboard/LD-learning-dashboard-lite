package com.upc.gessi.qrapids.app.presentation.web.mapping;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller("/QualityFactor")
public class QualityFactorsController {

    @GetMapping("/QualityFactors/CurrentChart")
    public String CurrentChart(){
        return "QualityFactors/CurrentChart";
    }

    @GetMapping("/QualityFactors/CurrentTable")
    public String CurrentTable(){
        return "QualityFactors/CurrentTable";
    }

    @GetMapping("/QualityFactors/HistoricTable")
    public String HistoricTable(){
        return "QualityFactors/HistoricTable";
    }

    @GetMapping("/QualityFactors/HistoricChart")
    public String HistoricChart(){
        return "QualityFactors/HistoricChart";
    }

    @GetMapping("/QualityFactors/Configuration")
    public String Config(){
        return "QualityFactors/AdditionalScreens/QualityFactorsConfig";
    }
}
