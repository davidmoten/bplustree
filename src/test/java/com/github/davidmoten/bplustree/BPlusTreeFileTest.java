package com.github.davidmoten.bplustree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.davidmoten.kool.Stream;
import org.junit.Test;

import com.github.davidmoten.bplustree.internal.file.LeafFile;
import com.github.davidmoten.guavamini.Lists;

public final class BPlusTreeFileTest {

    private static BPlusTree<Integer, Integer> create(int maxKeys) {

        return BPlusTree.file() //
                .directory(Testing.newDirectory()) //
                .clearDirectory() //
                .maxKeys(maxKeys) //
                .segmentSizeMB(1) //
                .keySerializer(Serializer.INTEGER) //
                .valueSerializer(Serializer.INTEGER) //
                .naturalOrder();
    }

    @Test
    public void testInsertOne() {
        BPlusTree<Integer, Integer> tree = create(2);
        tree.insert(3, 10);
        tree.commit();
        LeafFile<Integer, Integer> leaf = (LeafFile<Integer, Integer>) tree.root();
        assertEquals(1, leaf.numKeys());
        assertEquals(3, (int) leaf.key(0));
        assertEquals(10, (int) leaf.value(0));
        NodeWrapper<Integer, Integer> t = NodeWrapper.root(tree);
        assertEquals(Arrays.asList(3), t.keys());
        assertEquals(10, (int) tree.findFirst(3));
        assertNull(tree.findFirst(4));
    }

    @Test
    public void testInsertTwo() {
        BPlusTree<Integer, Integer> tree = create(2);
        tree.insert(3, 10);
        tree.insert(5, 20);
        LeafFile<Integer, Integer> leaf = (LeafFile<Integer, Integer>) tree.root();
        assertEquals(2, leaf.numKeys());
        assertEquals(3, (int) leaf.key(0));
        assertEquals(10, (int) leaf.value(0));
        assertEquals(5, (int) leaf.key(1));
        assertEquals(20, (int) leaf.value(1));
        NodeWrapper<Integer, Integer> t = NodeWrapper.root(tree);
        assertEquals(Arrays.asList(3, 5), t.keys());
        assertEquals(10, (int) tree.findFirst(3));
        assertEquals(20, (int) tree.findFirst(5));
    }

    @Test
    public void testInsertTwoReverseOrderWhichTestsInsertMethodOnLeaf() {
        BPlusTree<Integer, Integer> tree = create(2);
        tree.insert(5, 20);
        tree.insert(3, 10);
        LeafFile<Integer, Integer> leaf = (LeafFile<Integer, Integer>) tree.root();
        assertEquals(2, leaf.numKeys());
        assertEquals(3, (int) leaf.key(0));
        assertEquals(10, (int) leaf.value(0));
        assertEquals(5, (int) leaf.key(1));
        assertEquals(20, (int) leaf.value(1));
        NodeWrapper<Integer, Integer> t = NodeWrapper.root(tree);
        assertEquals(Arrays.asList(3, 5), t.keys());
        assertEquals(10, (int) tree.findFirst(3));
        assertEquals(20, (int) tree.findFirst(5));
    }

    @Test
    public void testInsertThreeWithMaxLeafKeysTwo() {
        BPlusTree<Integer, Integer> tree = create(2);
        tree.insert(3, 10);
        tree.insert(5, 20);
        tree.insert(7, 30);
        NodeWrapper<Integer, Integer> t = NodeWrapper.root(tree);
        assertEquals(Arrays.asList(5), t.keys());
        List<NodeWrapper<Integer, Integer>> children = t.children();
        assertEquals(2, children.size());
        assertEquals(Arrays.asList(3), children.get(0).keys());
        assertEquals(Arrays.asList(5, 7), children.get(1).keys());
        assertEquals(10, (int) tree.findFirst(3));
        assertEquals(20, (int) tree.findFirst(5));
        assertEquals(30, (int) tree.findFirst(7));
    }
    
    @Test
    public void testInsertMany() {
        int numKeysPerNode = Integer.parseInt(System.getProperty("numKeys", "32"));
        int iterations = Integer.parseInt(System.getProperty("n", "1"));
        for (int j = 0; j < iterations; j++) {
            long t = System.currentTimeMillis();
            int n = 1000000;
            {
                BPlusTree<Integer, Integer> tree = create(numKeysPerNode);
                for (int i = 1; i <= n; i++) {
                    int v = n - i + 1;
                    tree.insert(v, v);
                }
                System.out.println("insert rate desc order= "
                        + (n * 1000.0 / (System.currentTimeMillis() - t)) + " per second");
            }
            {
                BPlusTree<Integer, Integer> tree = create(numKeysPerNode);
                for (int i = 1; i <= n; i++) {
                    tree.insert(i, i);
                }
                System.out.println("insert rate asc order = "
                        + (n * 1000.0 / (System.currentTimeMillis() - t)) + " per second");
            }
        }
    }

    @Test
    public void testRegexSpeed() {
        String s = "2019-11-06 23:13:00.427 DEBUG com.zaxxer.hikari.pool.HikariPool [HikariPool-2 housekeeper] - HikariPool-2 - Before cleanup stats (total=5, active=3, idle=2, waiting=0)";
        Pattern p = Pattern.compile(
                "^.*com.zaxxer.hikari.pool.HikariPool.*Before cleanup stats.*, active=([0-9]+).*$");
        long t = System.currentTimeMillis();
        int n = 100000;
        for (int i = 0; i < n; i++) {
            Matcher m = p.matcher(s);
            if (m.find()) {
                if (m.group(1).equals("blah")) {
                    System.out.println("hello");
                }
            }
        }
        System.out.println("regex match rate = " + n * 1000.0 / (System.currentTimeMillis() - t)
                + " lines per second");
    }

    @Test
    public void testWithBiggishInts() {
        BPlusTree<Integer, Integer> tree = BPlusTree //
                .file() //
                .directory("target/bigints") //
                .clearDirectory() //
                .keySerializer(Serializer.INTEGER) //
                .valueSerializer(Serializer.INTEGER) //
                .comparator((a, b) -> Integer.compare(a, b));
        tree.insert(-1220935264, 1);
        tree.insert(110327396, 2);
        tree.insert(99162322, 3);
        assertEquals(Arrays.asList(1, 3, 2), Stream.from(tree.findAll()).toList().get());
    }

    @Test
    public void testInsert3201() throws Exception {
        try (BPlusTree<Integer, Integer> tree = BPlusTree //
                .file() //
                .directory("target/insertSome") //
                .clearDirectory() //
                .maxKeys(2) //
                .keySerializer(Serializer.INTEGER) // l
                .valueSerializer(Serializer.INTEGER) //
                .comparator((a, b) -> Integer.compare(a, b))) {
            tree.insert(3, 300);
            tree.insert(2, 200);
            tree.insert(0, 0);
            tree.insert(1, 100);
            assertEquals(Lists.newArrayList(0, 100, 200, 300),
                    Stream.from(tree.findAll()).toList().get());
        }
    }

    @Test
    public void testFindAllNextWhenNone() {
        BPlusTree<Integer, Integer> tree = BPlusTree //
                .file() //
                .directory("target/findall") //
                .clearDirectory() //
                .maxKeys(2) //
                .keySerializer(Serializer.INTEGER) //
                .valueSerializer(Serializer.INTEGER) //
                .comparator((a, b) -> Integer.compare(a, b));
        Iterator<Integer> it = tree.findAll().iterator();
        assertFalse(it.hasNext());
        try {
            it.next();
            org.junit.Assert.fail();
        } catch (NoSuchElementException e) {
            // ok
        }
    }

//    public static void main(String[] args) {
//        BPlusTree<Long, Long> tree = BPlusTree //
//                .file() //
//                .directory(Testing.newDirectory()) //
//                .maxKeys(8) //
//                .keySerializer(Serializer.LONG) //
//                .valueSerializer(Serializer.LONG) //
//                .naturalOrder();
//        long i = 1;
//        long t = System.currentTimeMillis();
//        while (true) {
//            tree.insert(i, i);
//            if (i % 1000000 == 0) {
//                long t2 = System.currentTimeMillis();
//                System.out.println(i / 1000000 + "m, insertRate=" + 1000000 * 1000.0 / (t2 - t)
//                        + " per second");
//                t = t2;
//            }
//            i++;
//        }
//    }

    public static void main(String[] args) {
        // fails after 2.4m entries inserted
        BPlusTree<Integer, Integer> tree = create(8);
        Random r = new Random(123456789);
        long count = 0;
        while (true) {
            int value = r.nextInt(100);
            tree.insert(value, value);
            count++;
            if (count % 100000 == 0) {
                System.out.println(count);
            }
        }
    }
    
}
