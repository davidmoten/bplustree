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
    public void testStructure() {
        BPlusTree<Integer, String> t = BPlusTree.builder().maxKeys(4).naturalOrder();
        t.insert(1, "ab");
        t.insert(1, "cd");
        assertEquals("cd", t.find(1));
        t.print();
        for (int i = 2; i < 100; i++) {
            t.insert(i, "a" + i);
        }
        System.out.println();
        t.print();
    }

    @Test
    public void testSplitsCorrect() {
        BPlusTree<Integer, Integer> t = BPlusTree.builder().maxKeys(2).naturalOrder();
        for (int i = 1; i <= 5; i++) {
            t.insert(i, i);
        }
        NodeWrapper<Integer, Integer> root = new NodeWrapper<Integer, Integer>(t.root());
        assertEquals(Arrays.asList(3),root.keys());
        List<NodeWrapper<Integer, Integer>> children = root.children();
        assertEquals(2, children.size());
        assertEquals(Arrays.asList(2), children.get(0).keys());
        assertEquals(Arrays.asList(4), children.get(1).keys());
    }
}
