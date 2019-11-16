package com.github.davidmoten.bplustree;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

public class Benchmarks {

    private static final LargeByteBuffer a = createLong();
    private static final LargeByteBuffer b = createVarlong();

    private static LargeByteBufferDelegating createLong() {
        LargeByteBufferDelegating b = new LargeByteBufferDelegating();
        b.putLong(1234567);
        return b;
    }

    private static LargeByteBufferDelegating createVarlong() {
        LargeByteBufferDelegating b = new LargeByteBufferDelegating();
        b.putVarlong(1234567);
        return b;
    }

    @State(Scope.Thread)
    public static class MyState {

        BPlusTree<Integer, Integer> tree;

        @Setup(Level.Trial)
        public void doSetup() {
            tree = BPlusTree.file().directory("target/bench") //
                    .clearDirectory() //
                    .deleteOnClose() //
                    .maxLeafKeys(16) //
                    .segmentSizeMB(50) //
                    .keySerializer(Serializer.INTEGER) //
                    .valueSerializer(Serializer.INTEGER) //
                    .naturalOrder();
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
            try {
                tree.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void storeOneMillionInts(MyState state) throws Exception {
        for (int i = 0; i < 1000000; i++) {
            state.tree.insert(i, i);
        }
    }

//    @Benchmark
    public long getLong() {
        a.position(0);
        return a.getLong();
    }

//    @Benchmark
    public long getVarlong() {
        b.position(0);
        return b.getVarlong();
    }
}
