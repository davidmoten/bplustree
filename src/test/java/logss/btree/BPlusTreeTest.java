package logss.btree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import com.github.davidmoten.guavamini.Lists;

public class BPlusTreeTest {

    @Test
    public void testFindOnEmptyTree() {
        BPlusTree<Integer, String> t = BPlusTree.builder().maxKeys(4).naturalOrder();
        assertNull(t.find(1));
    }

    @Test
    public void testAddElementAndFind() {
        BPlusTree<Integer, String> t = BPlusTree.builder().maxKeys(4).naturalOrder();
        t.insert(1, "boo");
        assertEquals("boo", t.find(1));
    }

    @Test
    public void testAddManyAndFind() {
        for (int m = 4; m <= 10; m++) {
            BPlusTree<Integer, String> t = BPlusTree.builder().maxKeys(m).naturalOrder();
            for (int n = 1; n < 1000; n++) {
                for (int i = 0; i < n; i++) {
                    t.insert(i, "a" + i);
                }
                for (int i = n - 1; i >= 0; i--) {
                    assertEquals("a" + i, t.find(i));
                }
            }
        }
    }

    @Test
    public void testAddManyShuffledAndFind() {
        for (int m = 4; m <= 10; m++) {
            BPlusTree<Integer, String> t = BPlusTree.builder().maxKeys(m).naturalOrder();
            for (int n = 1; n <= 1000; n++) {
                List<Integer> list = IntStream.range(0, n).boxed().collect(Collectors.toList());
                Collections.shuffle(list);
                for (int i = 0; i < n; i++) {
                    t.insert(list.get(i), "a" + list.get(i));
                }
                for (int i = n - 1; i >= 0; i--) {
                    assertEquals("a" + i, t.find(i));
                }
            }
        }
    }

    @Test
    public void testSplitsCorrect() {
        // verified with
        // https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
        BPlusTree<Integer, Integer> t = BPlusTree.builder().maxKeys(2).naturalOrder();
        for (int i = 1; i <= 5; i++) {
            t.insert(i, i);
        }
        NodeWrapper<Integer, Integer> root = NodeWrapper.root(t);
        assertEquals(Arrays.asList(3), root.keys());
        List<NodeWrapper<Integer, Integer>> children = root.children();
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

    @Test
    public void testStructureCorrect3Entries() {
        // verified with
        // https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
        BPlusTree<Integer, Integer> t = BPlusTree.builder().maxKeys(2).naturalOrder();
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

    @Test
    public void testStructureCorrect4Entries() {
        // verified with
        // https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
        BPlusTree<Integer, Integer> t = BPlusTree.builder().maxKeys(2).naturalOrder();
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

    @Test
    public void testPrint() {
        BPlusTree<Integer, Integer> t = BPlusTree.builder().maxKeys(2).naturalOrder();
        for (int i = 1; i <= 10; i++) {
            t.insert(i, i);
        }
        t.print();
    }

    @Test
    public void testFindRange() {
        BPlusTree<Integer, Integer> t = BPlusTree.builder().maxKeys(2).naturalOrder();
        for (int i = 1; i <= 10; i++) {
            t.insert(i, i);
        }
        assertEquals(Arrays.asList(1), toList(t.find(1, 2)));
        assertEquals(Arrays.asList(1, 2), toList(t.find(1, 3)));
        assertEquals(Arrays.asList(9, 10), toList(t.find(9, 11)));
        assertEquals(Arrays.asList(), toList(t.find(11, 20)));
        assertEquals(Arrays.asList(), toList(t.find(-3, -1)));
    }

    private static <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    @Test
    public void testDuplicateSupportedAndInOrderOfInsert() {
        BPlusTree<Integer, Integer> t = BPlusTree.builder().maxKeys(2).naturalOrder();
        t.insert(1, 2);
        t.insert(1, 3);
        t.print();
        assertEquals(Lists.newArrayList(2, 3), toList(t.find(0,4)));
    }

}
