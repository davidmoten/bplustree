package com.github.davidmoten.bplustree;

import java.io.File;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
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
            tree = BPlusTree //
                    .file() //
                    .directory("target/bench") //
                    .clearDirectory() //
                    .deleteOnClose() //
                    .maxLeafKeys(32) //
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
    public static class MapDbState {

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

    private static final int MANY = 2000000;

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void storeManyIntsBplusTree(MyState state) throws Exception {
        for (int i = 0; i < MANY; i++) {
            state.tree.insert(i, i);
        }
    }

//    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void storeManyIntsMapDb(MapDbState state) {
        for (int i = 0; i < MANY; i++) {
            state.tree.put(i, i);
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
