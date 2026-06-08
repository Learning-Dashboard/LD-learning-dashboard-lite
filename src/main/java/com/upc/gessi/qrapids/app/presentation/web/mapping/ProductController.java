package com.upc.gessi.qrapids.app.presentation.web.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/Products")
public class ProductController {

    @GetMapping("/Products/Configuration")
    public String Products(){
        return "Product/Products";
    }

    @GetMapping("/Products/Evaluation")
    public String ProductEvaluation(){
        return "Product/ProductEvaluation";
    }

    @GetMapping("/Products/DetailedEvaluation")
    public String ProductDetailedEvaluation(){
        return "Product/DetailedProductEvaluation";
    }
}
