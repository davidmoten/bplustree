package logss.btree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class BPlusTreeTest {

    @Test
    public void test() {
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

}
