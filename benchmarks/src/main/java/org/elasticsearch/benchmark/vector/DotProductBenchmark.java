/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.benchmark.vector;

import org.elasticsearch.script.field.vectors.KnnDenseVector;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Fork(2)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@OperationsPerInvocation(1000000)
@State(Scope.Benchmark)
public class DotProductBenchmark {

    float[] docVector = new float[98];
    float[] queryVector = new float[98];

    @Setup
    public void build() {
        for (int i = 0; i < 98; ++i) {
            docVector[i] = 98 - i;
            queryVector[i] = i;
        }
    }

    @Benchmark
    public void benchmark() throws IOException {
        for (int i = 0; i < 1000000; ++i) {
            new KnnDenseVector(docVector).dotProduct(queryVector);
        }
    }
}
