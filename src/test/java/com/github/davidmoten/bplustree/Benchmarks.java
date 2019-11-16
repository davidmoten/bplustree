package com.github.davidmoten.bplustree;

import java.io.File;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

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

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void storeOneMillionInts() throws Exception {
        try (BPlusTree<Integer, Integer> tree = BPlusTree.file().directory("target/bench") //
                .clearDirectory() //
                .deleteOnClose() //
                .maxLeafKeys(16) //
                .segmentSizeMB(50) //
                .keySerializer(Serializer.INTEGER) //
                .valueSerializer(Serializer.INTEGER) //
                .naturalOrder()) {
            for (int i = 0; i < 1000000; i++) {
                tree.insert(i, i);
            }
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
