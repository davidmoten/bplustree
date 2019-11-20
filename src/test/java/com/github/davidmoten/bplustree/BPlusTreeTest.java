package com.github.davidmoten.bplustree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.davidmoten.kool.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.github.davidmoten.guavamini.Lists;

@RunWith(Parameterized.class)
public class BPlusTreeTest {

    private static final Function<Integer, BPlusTree<Integer, Integer>> creatorFile = maxKeys -> {

        return BPlusTree.file() //
                .directory(Testing.newDirectory()) //
                .clearDirectory() //
                .deleteOnClose() //
                .maxKeys(maxKeys) //
                .uniqueKeys(false) //
                .keySerializer(Serializer.INTEGER) //
                .valueSerializer(Serializer.INTEGER) //
                .naturalOrder();
    };

    private static final Function<Integer, BPlusTree<Integer, Integer>> creatorMemory = maxKeys -> BPlusTree
            .memory().maxKeys(maxKeys).naturalOrder();

    @Parameters
    public static Collection<Object[]> creators() {
        return Arrays.asList(new Object[][] { { creatorMemory }, { creatorFile } });
    }

    private final Function<Integer, BPlusTree<Integer, Integer>> creator;

    public BPlusTreeTest(Function<Integer, BPlusTree<Integer, Integer>> creator) {
        this.creator = creator;
    }

    private BPlusTree<Integer, Integer> create(int maxKeys) {
        return creator.apply(maxKeys);
    }

    private static BPlusTree<Integer, String> createWithStringValue(int maxKeys) {
        return BPlusTree.memory().maxKeys(maxKeys).naturalOrder();
    }

    @Test
    public void testFindOnEmptyTree() throws Exception {
        try (BPlusTree<Integer, String> t = createWithStringValue(4)) {
            assertNull(t.findFirst(1));
        }
    }

    @Test
    public void testAddElementAndFind() throws Exception {
        try (BPlusTree<Integer, String> t = createWithStringValue(4)) {
            t.insert(1, "boo");
            assertEquals("boo", t.findFirst(1));
        }
    }

    @Test
    public void testAddManyAndFind() throws Exception {
        for (int m = 4; m <= 10; m++) {
            try (BPlusTree<Integer, String> t = createWithStringValue(m)) {
                for (int n = 1; n < 1000; n++) {
                    for (int i = 0; i < n; i++) {
                        t.insert(i, "a" + i);
                    }
                    for (int i = n - 1; i >= 0; i--) {
                        assertEquals("a" + i, t.findFirst(i));
                    }
                }
            }
        }
    }

    @Test
    public void testAddManyShuffledAndFind() throws Exception {
        for (int m = 4; m <= 10; m++) {
            try (BPlusTree<Integer, String> t = createWithStringValue(m)) {
                for (int n = 1; n <= 1000; n++) {
                    List<Integer> list = IntStream.range(0, n).boxed().collect(Collectors.toList());
                    Collections.shuffle(list);
                    for (int i = 0; i < n; i++) {
                        t.insert(list.get(i), "a" + list.get(i));
                    }
                    for (int i = n - 1; i >= 0; i--) {
                        assertEquals("a" + i, t.findFirst(i));
                    }
                }
            }
        }
    }

    @Test
    public void testSplitsCorrect3Entries() throws Exception {
        // verified with
        // https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
        try (BPlusTree<Integer, Integer> t = create(2)) {
            for (int i = 1; i <= 3; i++) {
                t.insert(i, i);
            }
            NodeWrapper<Integer, Integer> root = NodeWrapper.root(t);
            assertEquals(Arrays.asList(2), root.keys());
            List<NodeWrapper<Integer, Integer>> children = root.children();
            assertEquals(2, children.size());
            assertEquals(Arrays.asList(1), children.get(0).keys());
            assertEquals(Arrays.asList(2, 3), children.get(1).keys());
        }
    }

    @Test
    public void testSplitsCorrect4Entries() throws Exception {
        // verified with
        // https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
        try (BPlusTree<Integer, Integer> t = create(2)) {
            for (int i = 1; i <= 4; i++) {
                t.insert(i, i);
            }
            NodeWrapper<Integer, Integer> root = NodeWrapper.root(t);
            assertEquals(Arrays.asList(2, 3), root.keys());
            List<NodeWrapper<Integer, Integer>> children = root.children();
            assertEquals(3, children.size());
            assertEquals(Arrays.asList(1), children.get(0).keys());
            assertEquals(Arrays.asList(2), children.get(1).keys());
            assertEquals(Arrays.asList(3, 4), children.get(2).keys());
        }
    }

//    @Test
//    public void testStructureWithRepeats() throws Exception {
//        try (BPlusTree<Integer, Integer> t = create(4)) {
//            t.insert(1, 10);
//            t.insert(1, 20);
//            t.insert(1, 30);
//            t.insert(1, 40);
//            t.insert(1, 50);
//            t.insert(1, 60);
//            t.print();
//            t.findOrderPreserving(0, 2).forEach(System.out::println);
//        }
//    }

    @Test
    public void testSplitsCorrect5Entries() throws Exception {
        // verified with
        // https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
        try (BPlusTree<Integer, Integer> t = create(2)) {
            for (int i = 1; i <= 5; i++) {
                t.insert(i, i);
            }
            NodeWrapper<Integer, Integer> root = NodeWrapper.root(t);
            assertEquals(Arrays.asList(3), root.keys());
            List<NodeWrapper<Integer, Integer>> children = root.children();
            assertEquals(Arrays.asList(3), root.keys());
            assertEquals(2, children.size());
            assertEquals(Arrays.asList(2), children.get(0).keys());
            assertEquals(Arrays.asList(4), children.get(1).keys());
            List<NodeWrapper<Integer, Integer>> a = children.get(0).children();
            List<NodeWrapper<Integer, Integer>> b = children.get(1).children();
            assertEquals(2, a.size());
            assertEquals(Arrays.asList(1), a.get(0).keys());
            assertEquals(Arrays.asList(2), a.get(1).keys());
            assertEquals(2, b.size());
            assertEquals(Arrays.asList(3), b.get(0).keys());
            assertEquals(Arrays.asList(4, 5), b.get(1).keys());
        }
    }

    @Test
    public void testStructureCorrect3Entries() throws Exception {
        // verified with
        // https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
        try (BPlusTree<Integer, Integer> t = create(2)) {
            for (int i = 1; i <= 3; i++) {
                t.insert(i, i);
            }
            NodeWrapper<Integer, Integer> root = NodeWrapper.root(t);
            assertEquals(Arrays.asList(2), root.keys());
            List<NodeWrapper<Integer, Integer>> children = root.children();
            assertEquals(2, children.size());
            assertEquals(Arrays.asList(1), children.get(0).keys());
            assertEquals(Arrays.asList(2, 3), children.get(1).keys());
        }
    }

    @Test
    public void testStructureCorrect4Entries() throws Exception {
        // verified with
        // https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
        try (BPlusTree<Integer, Integer> t = create(2)) {
            for (int i = 1; i <= 4; i++) {
                t.insert(i, i);
            }
            NodeWrapper<Integer, Integer> root = NodeWrapper.root(t);
            assertEquals(Arrays.asList(2, 3), root.keys());
            List<NodeWrapper<Integer, Integer>> children = root.children();
            assertEquals(3, children.size());
            assertEquals(Arrays.asList(1), children.get(0).keys());
            assertEquals(Arrays.asList(2), children.get(1).keys());
            assertEquals(Arrays.asList(3, 4), children.get(2).keys());
        }
    }

    @Test
    public void testPrint() throws Exception {
        try (BPlusTree<Integer, Integer> t = create(2)) {
            for (int i = 1; i <= 10; i++) {
                t.insert(i, i);
            }
            t.print();
        }
    }

    @Test
    public void testNextWithFindOneAtStart() throws Exception {
        try (BPlusTree<Integer, Integer> t = create(2)) {
            for (int i = 1; i <= 10; i++) {
                t.insert(i, i);
            }
            Iterator<Integer> it = t.find(1, 2).iterator();
            assertTrue(it.hasNext());
            assertEquals(1, (int) it.next());
            assertFalse(it.hasNext());
        }
    }

    @Test
    public void testNextWithFindTwoAtStart() throws Exception {
        try (BPlusTree<Integer, Integer> t = create(2)) {
            for (int i = 1; i <= 10; i++) {
                t.insert(i, i);
            }
            Iterator<Integer> it = t.find(1, 3).iterator();
            assertTrue(it.hasNext());
            assertEquals(1, (int) it.next());
            assertTrue(it.hasNext());
            assertEquals(2, (int) it.next());
            assertFalse(it.hasNext());
        }
    }

    @Test
    public void testNextWithFindTwoOverlappingEnd() throws Exception {
        try (BPlusTree<Integer, Integer> t = create(2)) {
            for (int i = 1; i <= 10; i++) {
                t.insert(i, i);
            }
            Iterator<Integer> it = t.find(9, 11).iterator();
            assertTrue(it.hasNext());
            assertEquals(9, (int) it.next());
            assertTrue(it.hasNext());
            assertEquals(10, (int) it.next());
            assertFalse(it.hasNext());
        }
    }

    @Test
    public void testNextWithFind2() throws Exception {
        try (BPlusTree<Integer, Integer> t = create(2)) {
            for (int i = 1; i <= 10; i++) {
                t.insert(i, i);
            }
            Iterator<Integer> it = t.find(1, 3).iterator();
            assertTrue(it.hasNext());
            assertEquals(1, (int) it.next());
            assertTrue(it.hasNext());
            assertEquals(2, (int) it.next());
            assertFalse(it.hasNext());
        }
    }

    @Test
    public void testFindRange() throws Exception {
        try (BPlusTree<Integer, Integer> t = create(2)) {
            for (int i = 1; i <= 10; i++) {
                t.insert(i, i);
            }
            assertEquals(Arrays.asList(1), toList(t.find(1, 2)));
            assertEquals(Arrays.asList(1, 2), toList(t.find(1, 3)));
            assertEquals(Arrays.asList(9, 10), toList(t.find(9, 11)));
            assertEquals(Arrays.asList(), toList(t.find(11, 20)));
            assertEquals(Arrays.asList(), toList(t.find(-3, -1)));
        }
    }

    private static <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    @Test
    public void testDuplicateSupportedAndInReverseOrderOfInsert() throws Exception {
        try (BPlusTree<Integer, Integer> t = create(2)) {
            t.insert(1, 2);
            t.insert(1, 3);
            assertEquals(Lists.newArrayList(3, 2), //
                    toList(t.find(0, 4)));
        }
    }

//    @Test
//    public void testDuplicateSupportedAndOrderPreservedBySpecialFindMethodManyDifferentKeys()
//            throws Exception {
//        try (BPlusTree<Integer, Integer> t = create(2)) {
//            t.insert(1, 12);
//            t.insert(1, 13);
//            t.insert(2, 21);
//            t.insert(2, 22);
//            t.insert(2, 23);
//            t.insert(3, 31);
//            assertEquals(Lists.newArrayList(12, 13, 21, 22, 23, 31), //
//                    toList(t.findOrderPreserving(0, 4)));
//        }
//    }

//    @Test
//    public void testDuplicateSupportedAndOrderPreservedBySpecialFindMethod() throws Exception {
//        try (BPlusTree<Integer, Integer> t = create(2)) {
//            t.insert(1, 12);
//            t.insert(1, 13);
//            t.insert(2, 21);
//            t.insert(2, 22);
//            t.insert(2, 23);
//            t.insert(3, 31);
//            assertEquals(Lists.newArrayList(12, 13, 21, 22, 23), //
//                    toList(t.findOrderPreserving(0, 3)));
//        }
//    }
    
//    @Test
//    public void testDuplicateSupportedAndOrderPreservedBySpecialFindMethod2() throws Exception {
//        try (BPlusTree<Integer, Integer> t = create(2)) {
//            t.insert(1, 10);
//            t.insert(1, 11);
//            t.insert(1, 12);
//            t.insert(1, 13);
//            t.insert(1, 14);
//            t.insert(1, 15);
//            assertEquals(Lists.newArrayList(10, 11, 12, 13, 14, 15), //
//                    toList(t.findOrderPreserving(0, 3)));
//        }
//    }

//    @Test
//    public void testDuplicateSupportedAndOrderPreservedBySpecialFindMethodAllKeysSame()
//            throws Exception {
//        try (BPlusTree<Integer, Integer> t = create(2)) {
//            t.insert(1, 2);
//            t.insert(1, 3);
//            assertEquals(Lists.newArrayList(2, 3), toList(t.findOrderPreserving(0, 4)));
//        }
//    }

    @Test
    public void testDuplicateNotSupportedWhenUniqueKeysSetToTrue() throws Exception {
        try (BPlusTree<Integer, Integer> t = BPlusTree.memory().maxKeys(2).uniqueKeys()
                .naturalOrder()) {
            t.insert(1, 2);
            t.insert(1, 3);
            assertEquals(Lists.newArrayList(3), toList(t.find(0, 4)));
        }
    }

    @Test
    public void testClearOversize() {
        List<Integer> list = Lists.newArrayList(1, 2, 3);
        List<Integer> list2 = BPlusTree.clear(list, 2);
        assertTrue(list2.isEmpty());
        assertTrue(list != list2);
    }

    @Test
    public void testClearUndersize() {
        List<Integer> list = Lists.newArrayList(1, 2, 3);
        List<Integer> list2 = BPlusTree.clear(list, 3);
        assertTrue(list2.isEmpty());
        assertTrue(list == list2);
    }

    @Test
    public void testInsertt3201() throws Exception {
        try (BPlusTree<Integer, Integer> tree = create(2)) {
            tree.insert(3, 300);
            tree.insert(2, 200);
            tree.insert(0, 0);
            tree.insert(1, 100);
            assertEquals(Lists.newArrayList(0, 100, 200, 300),
                    Stream.from(tree.findAll()).toList().get());
        }
    }

    @Test
    public void testInsert43210() throws Exception {
        try (BPlusTree<Integer, Integer> tree = create(2)) {
            tree.insert(4, 400);
            tree.insert(3, 300);
            tree.insert(2, 200);
            tree.insert(1, 100);
            tree.insert(0, 7);
            assertEquals(Lists.newArrayList(7, 100, 200, 300, 400),
                    Stream.from(tree.findAll()).toList().get());
        }
    }

    @Test
    public void testInsert31024() throws Exception {
        try (BPlusTree<Integer, Integer> tree = create(2)) {
            tree.insert(3, 300);
            tree.insert(1, 100);
            tree.insert(0, 7);
            tree.insert(2, 200);
            tree.insert(4, 400);
            assertEquals(Lists.newArrayList(7, 100, 200, 300, 400),
                    Stream.from(tree.findAll()).toList().get());
        }
    }

    @Test
    public void testTreeOnAllPermutationsOfNonRepeatedInput() throws Exception {
        for (int maxKeys = 2; maxKeys <= 5; maxKeys++) {
            int mk = maxKeys;
            for (int i = 5; i <= 5; i++) {
                List<Integer> expected = new ArrayList<>();
                for (int j = 0; j < i; j++) {
                    expected.add(j);
                }
                Stream.permutations(i).doOnNext(list -> {
                    try (BPlusTree<Integer, Integer> tree = create(mk)) {
                        for (int v : list) {
                            tree.insert(v, v);
                        }
                        // assertEquals(0, (int) tree.firstLeaf(tree.root()).key(0));
                        assertEquals(expected, Stream.from(tree.findAll()).toList().get());
                    }
                }).forEach();
            }
        }
    }

    @Test
    public void testLeafToString() throws Exception {
        try (BPlusTree<Integer, Integer> tree = create(2)) {
            tree.insert(1, 1);
            assertTrue(tree.root().toString().startsWith("Leaf"));
        }
    }

    @Test
    public void testNonLeafToString() throws Exception {
        try (BPlusTree<Integer, Integer> tree = create(2)) {
            tree.insert(1, 1);
            tree.insert(2, 2);
            tree.insert(3, 3);
            assertTrue(tree.root().toString().startsWith("NonLeaf"));
        }
    }

    @Test
    public void testFindOneKey() throws Exception {
        try (BPlusTree<Integer, Integer> tree = create(2)) {
            tree.insert(1, 10);
            tree.insert(2, 20);
            Iterator<Integer> it = tree.find(1).iterator();
            assertEquals(10, (int) it.next());
            assertFalse(it.hasNext());
        }
    }

    @Test
    public void testFindOneKeyNotFound() throws Exception {
        try (BPlusTree<Integer, Integer> tree = create(2)) {
            tree.insert(1, 10);
            tree.insert(2, 20);
            Iterator<Integer> it = tree.find(3).iterator();
            assertFalse(it.hasNext());
        }
    }

    @Test
    public void testFindEntries() throws Exception {
        try (BPlusTree<Integer, Integer> tree = create(2)) {
            tree.insert(1, 10);
            tree.insert(2, 20);
            Iterator<Entry<Integer, Integer>> it = tree.findEntries(1, 2, true).iterator();
            Entry<Integer, Integer> entry = it.next();
            assertEquals(1, (int) entry.key());
            assertEquals(10, (int) entry.value());
            entry = it.next();
            assertEquals(2, (int) entry.key());
            assertEquals(20, (int) entry.value());
            assertFalse(it.hasNext());
        }
    }
}
