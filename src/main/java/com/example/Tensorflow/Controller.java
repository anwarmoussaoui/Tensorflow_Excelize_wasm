package com.example.Tensorflow;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.DoubleStream;

@org.springframework.stereotype.Controller
public class Controller {
    private final ContextPool contextPool;
    public Controller(ContextPool contextPool){

        this.contextPool = contextPool;
    }

    @PostMapping("/predict")
    public String predictPrice(
            @RequestParam double bedrooms,
            @RequestParam double bathrooms,
            @RequestParam double sqftLiving,
            @RequestParam double sqftLot,
            @RequestParam double floors,
            @RequestParam double waterfront,
            @RequestParam double view,
            @RequestParam double condition,
            @RequestParam double sqftAbove,
            @RequestParam double sqftBasement,
            @RequestParam double yrBuilt,
            @RequestParam double yrRenovated,
            Model model) {

        double[] newHouse = {
                bedrooms, bathrooms, sqftLiving, sqftLot,
                floors, waterfront, view, condition,
                sqftAbove, sqftBasement, yrBuilt, yrRenovated
        };

        Context context = contextPool.getContext();
        Value predictFn = context.getBindings("js").getMember("predictHouse");

        Value jsArray = context.eval("js", "Array");
        Value input = jsArray.newInstance((Object[]) DoubleStream.of(newHouse).boxed().toArray());

        Value prediction = predictFn.execute(input);
        Value arrayPromise = prediction.invokeMember("array");

        context.eval("js", "var wait = async (p) => await p;");
        Value result = context.getBindings("js").getMember("wait").execute(arrayPromise);

        double price = result.getArrayElement(0).getArrayElement(0).asDouble();
        long roundedPrice = Math.round(price);

        model.addAttribute("predictedPrice", roundedPrice);
        return "index";
    }
}
