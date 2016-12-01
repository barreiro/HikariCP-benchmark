/*
 * Copyright (C) 2014 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaxxer.hikari.benchmark;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import sun.misc.Contended;

//@State(Scope.Benchmark)
//@Warmup(iterations=3, batchSize=1_000_000)
//@Measurement(iterations=8, batchSize=1_000_000)
//@BenchmarkMode(Mode.SingleShotTime)
//@OutputTimeUnit(TimeUnit.NANOSECONDS)

@Warmup(iterations=3)
@Measurement(iterations=8)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)

//@Warmup(iterations=3)
//@Measurement(iterations=8)
//@BenchmarkMode(Mode.SampleTime)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ConnectionBench extends BenchBase {

    @State( Scope.Benchmark )
    public static class BenchmarkState {
        private static final Random RANDOM = new Random();
    }

    @State( Scope.Thread )
    public static class ThreadState {

        @Contended
        private volatile Random random;

        @Setup
        public void setupContext(BenchmarkState state) throws Throwable {
            random = new Random( state.RANDOM.nextLong() );
        }
    }

    @Benchmark
    @CompilerControl( CompilerControl.Mode.INLINE )
    public static Connection cycleConnection(ThreadState state) throws SQLException {
        Connection connection = DS.getConnection();

        // Do some work
        //doWork( false, state.random.nextInt() );

        // Yeld!
        //doYeld( false );

        // Wait some time (5ms average)
        //doSleep( false, state.random.nextInt( 2 ) );

        // Do some work
        //doWork( false, state.random.nextInt( 1000 * 1 ) );

        connection.close();
        return connection;
    }


    public static void doWork(boolean b, long amount) {
        if ( b ) {
            Blackhole.consumeCPU( amount );
        }
    }

    public static void doYeld(boolean b) {
        if ( b ) {
            Thread.yield();
        }
    }

    public static void doSleep(boolean b, long amount) {
        if ( b ) {
            try {
                Thread.sleep( amount );
            } catch ( InterruptedException ignore ) { }
        }
    }

}
