package com.github.davidmoten.bplustree.internal;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Test;

public class NonLeafTest {

    @Test
//    @Ignore
    public void testGetLocationUniqueKeys() {
        NonLeaf<Integer, Integer> n = create(1, 3, 5);
        assertEquals(0, Util.getLocation(n, 0, Comparator.naturalOrder()));
        assertEquals(0, Util.getLocation(n, 1, Comparator.naturalOrder()));
        assertEquals(1, Util.getLocation(n, 2, Comparator.naturalOrder()));
        assertEquals(1, Util.getLocation(n, 3, Comparator.naturalOrder()));
        assertEquals(2, Util.getLocation(n, 4, Comparator.naturalOrder()));
        assertEquals(2, Util.getLocation(n, 5, Comparator.naturalOrder()));
        assertEquals(3, Util.getLocation(n, 6, Comparator.naturalOrder()));
    }

    @Test
    public void testGetLocationNonUniqueKeys() {
        NonLeaf<Integer, Integer> n = create(1, 1, 3, 3);
        assertEquals(0, Util.getLocation(n, 0, Comparator.naturalOrder()));
        assertEquals(2, Util.getLocation(n, 1, Comparator.naturalOrder()));
        assertEquals(2, Util.getLocation(n, 2, Comparator.naturalOrder()));
        assertEquals(4, Util.getLocation(n, 3, Comparator.naturalOrder()));
        assertEquals(4, Util.getLocation(n, 4, Comparator.naturalOrder()));
    }
    
    @Test
    public void testGetLocationNonUniqueKeysOldMethod() {
        NonLeaf<Integer, Integer> n = create(1, 1, 3, 3);
        assertEquals(0, Util.getLocationOld(n, 0, Comparator.naturalOrder()));
        assertEquals(2, Util.getLocationOld(n, 1, Comparator.naturalOrder()));
        assertEquals(2, Util.getLocationOld(n, 2, Comparator.naturalOrder()));
        assertEquals(4, Util.getLocationOld(n, 3, Comparator.naturalOrder()));
        assertEquals(4, Util.getLocationOld(n, 4, Comparator.naturalOrder()));
    }

    private NonLeaf<Integer, Integer> create(int... keys) {
        return new NonLeaf<Integer, Integer>() {

            @Override
            public Options<Integer, Integer> options() {
                return null;
            }

            @Override
            public Factory<Integer, Integer> factory() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setNumKeys(int numKeys) {
                // TODO Auto-generated method stub

            }

            @Override
            public int numKeys() {
                return keys.length;
            }

            @Override
            public void setChild(int i, Node<Integer, Integer> node) {
                // TODO Auto-generated method stub

            }

            @Override
            public Node<Integer, Integer> child(int i) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Integer key(int i) {
                return keys[i];
            }

            @Override
            public void setKey(int i, Integer key) {
                // TODO Auto-generated method stub

            }

            @Override
            public void move(int mid, NonLeaf<Integer, Integer> other, int length) {
                // TODO Auto-generated method stub

            }

            @Override
            public void insert(int i, Integer key, Node<Integer, Integer> left) {
                // TODO Auto-generated method stub

            }

        };

    }

}
