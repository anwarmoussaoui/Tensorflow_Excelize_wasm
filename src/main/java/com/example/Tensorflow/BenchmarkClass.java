package com.example.Tensorflow;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;


@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 3, time = 10)
@Fork(1)

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)

public class BenchmarkClass {
    ContextPool contextPool;

    public BenchmarkClass()  {

        try {
            this.contextPool = new ContextPool();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize ContextPool", e);
        }

    }

    @Benchmark
    public void predict(Blackhole blackhole){
            Context context = contextPool.getContext();
            try {
                GenerateFunction predictFn = context.getBindings("js").getMember("predictHouse").as(GenerateFunction.class);
                double[] newHouse = new double[12];
                for (int i = 0; i < newHouse.length; i++) {
                    newHouse[i] = ThreadLocalRandom.current().nextDouble(1, 5000);
                }
                Value jsArray = context.eval("js", "Array");
                Value input = jsArray.newInstance((Object[]) DoubleStream.of(newHouse).boxed().toArray());

                Prediction prediction = predictFn.apply(input);

                prediction.then(result -> {

                    blackhole.consume(result[0].getArrayElement(0).getArrayElement(0));
                    return null;
                });
            }finally {
                contextPool.release(context);
            }

        }
    @FunctionalInterface
    public interface GenerateFunction {
        Prediction apply(Value input);
    }

    public interface Prediction {
        void then(ProxyExecutable callback);
    }
}
