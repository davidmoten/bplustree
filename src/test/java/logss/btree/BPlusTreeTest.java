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

}
