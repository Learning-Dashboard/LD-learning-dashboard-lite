package com.upc.gessi.qrapids.app.presentation.web.mapping;


import com.upc.gessi.qrapids.app.domain.adapters.QMA.QMAQualityFactors;
import com.upc.gessi.qrapids.app.domain.adapters.QMA.QMAStrategicIndicators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/StrategicIndicators")
public class StrategicIndicatorsController {

    @Autowired
    private QMAStrategicIndicators qmasi;

    @Autowired
    private QMAQualityFactors qmaqf;

/*
    @GetMapping("/")
    public String index(){
        return "redirect:/StrategicIndicators/CurrentChart";
    }
*/
    @GetMapping("/StrategicIndicators/CurrentChart")
    public String CurrentChart(){ return "StrategicIndicators/CurrentChart"; }

    @GetMapping("/StrategicIndicators/CurrentTable")
    public String CurrentTable(){ return "StrategicIndicators/CurrentTable"; }

    @GetMapping("/StrategicIndicators/HistoricTable")
    public String HistoricTable(){
        return "StrategicIndicators/HistoricTable";
    }

    @GetMapping("/StrategicIndicators/HistoricChart")
    public String HistoricChart(){
        return "StrategicIndicators/HistoricChart";
    }

    @GetMapping("/StrategicIndicators/New")
    public String NewStrategicIndicator(){
        if (qmasi.isCategoriesEmpty() && qmaqf.isCategoriesEmpty())
            return "StrategicIndicators/AdditionalScreens/CreateCategories";
        else
            return "StrategicIndicators/AdditionalScreens/NewStrategicIndicator";
    }

    @GetMapping("/EditStrategicIndicators/{id}")
    public String EditStrategicIndicator(@PathVariable Long id){
        return "StrategicIndicators/AdditionalScreens/NewStrategicIndicator";
    }

    @GetMapping("/StrategicIndicators/Feedback")
    public String Feedback(){
        return "StrategicIndicators/AdditionalScreens/Feedback";
    }

    @GetMapping("/StrategicIndicators/FeedbackReport")
    public String FeedbackReport(){
        return "StrategicIndicators/AdditionalScreens/FeedbackReport";
    }

    @GetMapping("/StrategicIndicators/Configuration")
    public String Config(){
        return "StrategicIndicators/AdditionalScreens/StrategicIndicatorsConfig";
    }

    @GetMapping("/Categories/Configuration")
    public String CategoriesConfig () {
        return "StrategicIndicators/AdditionalScreens/CategoriesConfig";
    }
}
