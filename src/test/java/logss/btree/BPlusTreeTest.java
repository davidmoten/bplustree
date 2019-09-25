package logss.btree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.PrintStream;
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
        t.print(0, System.out);
        for (int i = 2; i < 100; i++) {
            t.insert(i, "a" + i);
        }
        System.out.println();
        t.print(0, System.out);
    }

}
