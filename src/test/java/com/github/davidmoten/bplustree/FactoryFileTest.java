package com.github.davidmoten.bplustree;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.davidmoten.bplustree.internal.Factory;
import com.github.davidmoten.bplustree.internal.file.LeafFile;
import com.github.davidmoten.bplustree.internal.file.NonLeafFile;

public class FactoryFileTest {

    @Test
    public void testLeafFile() throws Exception {
        try (BPlusTree<Integer, Long> t = create()) {
            Factory<Integer, Long> factory = t.factory();
            LeafFile<Integer, Long> leaf = (LeafFile<Integer, Long>) factory.createLeaf();
            LeafFile<Integer, Long> leaf2 = (LeafFile<Integer, Long>) factory.createLeaf();
            leaf.setNumKeys(2);
            assertEquals(2, leaf.numKeys());
            leaf.setNumKeys(0);
            assertEquals(0, leaf.numKeys());
            leaf.setNext(leaf2);
            assertEquals(leaf2.position(), leaf.next().position());
            leaf.insert(3, Long.MAX_VALUE);
            assertEquals(1, leaf.numKeys());
            assertEquals(3, (int) leaf.key(0));
            leaf.setValue(0, 1234567890L);
            assertEquals(1234567890L, (long) leaf.value(0));
        }
    }

    @Test
    public void testNonLeafFile() throws Exception {
        try (BPlusTree<Integer, Long> t = create()) {
            Factory<Integer, Long> factory = t.factory();
            NonLeafFile<Integer, Long> n = (NonLeafFile<Integer, Long>) factory.createNonLeaf();
            n.setNumKeys(2);
            assertEquals(2, n.numKeys());
            n.setNumKeys(0);
            assertEquals(0, n.numKeys());
        }
    }

    private BPlusTree<Integer, Long> create() {
        return BPlusTree //
                .file() //
                .directory("target/facfile") //
                .clearDirectory() //
                .maxKeys(3) //
                .keySerializer(Serializer.INTEGER) //
                .valueSerializer(Serializer.LONG) //
                .naturalOrder();
    }

}
