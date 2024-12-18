package com.dnu.ffecs;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Fork(value = 1)
@State(Scope.Thread)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class ParallelStreamBenchmark {

    private List<Long> numbers;

    @Setup(Level.Trial)
    public void setup() {
        numbers = new Random().longs(10_000_000, 1, 101)
                .boxed().collect(Collectors.toList());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public long computeSum(Blackhole blackhole) {
        long sum = numbers.parallelStream().mapToLong(Long::longValue).sum();
        blackhole.consume(sum);
        return sum;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double computeAverage(Blackhole blackhole) {
        double average = numbers.parallelStream().mapToLong(Long::longValue)
                .average().orElseThrow(RuntimeException::new);
        blackhole.consume(average);
        return average;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double calculateStdDev(Blackhole blackhole) {
        double mean = computeAverage(blackhole);
        double stdDev = Math.sqrt(numbers.parallelStream().mapToDouble(num -> Math.pow(num - mean, 2))
                .average().orElseThrow(RuntimeException::new));
        blackhole.consume(stdDev);
        return stdDev;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public List<Long> doubleValues(Blackhole blackhole) {
        List<Long> doubledValues = numbers.parallelStream().map(num -> num * 2).collect(Collectors.toList());
        blackhole.consume(doubledValues);
        return doubledValues;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public List<Long> evenMultiples(Blackhole blackhole) {
        List<Long> multiples = numbers.parallelStream().filter(num -> num % 2 == 0 && num % 3 == 0).collect(Collectors.toList());
        blackhole.consume(multiples);
        return multiples;
    }
}
