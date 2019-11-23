package com.github.davidmoten.bplustree;

import java.io.File;
import java.util.Iterator;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

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
    
    private static final int MAX_KEYS = 8;
    private static final int MANY = 2000000;
    private static final int NON_EMPTY_COUNT = 1000000;
    private static final int TIME_SECONDS = 10;
    private static final int ITERATIONS = 5;
    private static final int WARMUP_ITERATIONS = 5;

    @State(Scope.Thread)
    public static class EmptyTree {

        BPlusTree<Integer, Integer> tree;

        @Setup(Level.Trial)
        public void doSetup() {
            tree = BPlusTree //
                    .file() //
                    .directory("target/bench") //
                    .clearDirectory() //
                    .deleteOnClose() //
                    .maxLeafKeys(MAX_KEYS) //
                    .segmentSizeMB(10) //
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

    @State(Scope.Thread)
    public static class EmptyTreeMapDb {

        private DB db;
        private BTreeMap<Integer, Integer> tree;

        @Setup(Level.Trial)
        public void doSetup() {
            db = DBMaker //
                    .fileDB(new File("target/mapdb")) //
                    .concurrencyDisable() //
                    .fileDeleteAfterClose() //
                    .fileMmapEnableIfSupported() //
                    .make();
            tree = db.treeMap("tree") //
                    .keySerializer(org.mapdb.Serializer.INTEGER) //
                    .valueSerializer(org.mapdb.Serializer.INTEGER) //
                    .createOrOpen();
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
            try {
                tree.close();
                db.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @State(Scope.Thread)
    public static class NonEmptyTree {

        BPlusTree<Integer, Integer> tree;

        @Setup(Level.Trial)
        public void doSetup() {
            tree = BPlusTree //
                    .file() //
                    .directory("target/bench") //
                    .clearDirectory() //
                    .deleteOnClose() //
                    .maxLeafKeys(MAX_KEYS) //
                    .segmentSizeMB(10) //
                    .keySerializer(Serializer.INTEGER) //
                    .valueSerializer(Serializer.INTEGER) //
                    .naturalOrder();
            for (int i = 0; i < NON_EMPTY_COUNT; i++) {
                tree.insert(i, i);
            }
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

    @State(Scope.Thread)
    public static class NonEmptyTreeMapDb {

        private DB db;
        private BTreeMap<Integer, Integer> tree;

        @Setup(Level.Trial)
        public void doSetup() {
            db = DBMaker.fileDB(new File("target/mapdb")) //
                    .concurrencyDisable() //
                    .fileDeleteAfterClose() //
                    .fileMmapEnableIfSupported() //
                    .make();
            tree = db.treeMap("tree") //
                    .keySerializer(org.mapdb.Serializer.INTEGER) //
                    .valueSerializer(org.mapdb.Serializer.INTEGER) //
                    .createOrOpen();
            for (int i = 0; i < NON_EMPTY_COUNT; i++) {
                tree.put(i, i);
            }
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
            try {
                tree.close();
                db.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Warmup(iterations = WARMUP_ITERATIONS, time = TIME_SECONDS)
    @Measurement(iterations = ITERATIONS, time = TIME_SECONDS)
    public void storeManyIntsBPlusTree(EmptyTree state) throws Exception {
        for (int i = 0; i < MANY; i++) {
            state.tree.insert(i, i);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Warmup(iterations = WARMUP_ITERATIONS, time = TIME_SECONDS)
    @Measurement(iterations = ITERATIONS, time = TIME_SECONDS)
    public void storeManyIntsMapDb(EmptyTreeMapDb state) {
        for (int i = 0; i < MANY; i++) {
            state.tree.put(i, i);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = WARMUP_ITERATIONS, time = TIME_SECONDS)
    @Measurement(iterations = ITERATIONS, time = TIME_SECONDS)
    public long rangeSearchManyIntsBPlusTree(NonEmptyTree state) {
        return count(state.tree.find(100000, 100000, true).iterator());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = WARMUP_ITERATIONS, time = TIME_SECONDS)
    @Measurement(iterations = ITERATIONS, time = TIME_SECONDS)
    public long rangeSearchManyIntsMapDb(NonEmptyTreeMapDb state) {
        return count(state.tree.valueIterator(100000, true, 100000, true));
    }

    private static long count(Iterator<?> it) {
        long count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }

    // @Benchmark
    public long getLong() {
        a.position(0);
        return a.getLong();
    }

    // @Benchmark
    public long getVarlong() {
        b.position(0);
        return b.getVarlong();
    }
}
