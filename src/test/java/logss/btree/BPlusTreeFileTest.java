package logss.btree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import logss.btree.internal.file.FactoryFile;
import logss.btree.internal.file.LeafFile;

@RunWith(Parameterized.class)
public class BPlusTreeFileTest {

    private static BPlusTree<Integer, Integer> create(int maxKeys) {
        Serializer<Integer> serializer = new Serializer<Integer>() {

            @Override
            public Integer read(ByteBuffer bb) {
                return bb.getInt();
            }

            @Override
            public void write(ByteBuffer bb, Integer t) {
                bb.putInt(t);
            }

            @Override
            public int maxSize() {
                return Integer.BYTES;
            }
        };

        return BPlusTree.<Integer, Integer>builder().factoryProvider(options -> new FactoryFile<Integer, Integer>( //
                options, //
                new File("target"), //
                serializer, serializer)).maxKeys(maxKeys) //
                .naturalOrder();
    }

    @Test
    public void testInsertOne() {
        BPlusTree<Integer, Integer> tree = create(2);
        tree.insert(3, 10);
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

}
