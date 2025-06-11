package com.example.Tensorflow;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 3, time = 10)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class BenchmarkRunner {

    private BenchmarkClass benchmarkClass;
    ContextPool contextPool;
    private byte[] modelData;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        this.contextPool =new ContextPool();
        benchmarkClass = new BenchmarkClass();
    }

    @Benchmark
    public void benchmarkPredict(Blackhole blackhole) {
        benchmarkClass.predict(blackhole);
    }
}
