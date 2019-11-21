package com.github.davidmoten.bplustree.internal;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Test;

public class NonLeafTest {

    @Test
    public void testGetLocationUniqueKeys() {
        Node<Integer, Integer> n = create(1, 3, 5);
        assertEquals(0, getLocation(n, 0));
        assertEquals(1, getLocation(n, 1));
        assertEquals(1, getLocation(n, 2));
        assertEquals(2, getLocation(n, 3));
        assertEquals(2, getLocation(n, 4));
        assertEquals(3, getLocation(n, 5));
        assertEquals(3, getLocation(n, 6));
    }
    
    private static int getLocation(Node<Integer, Integer> n, int key) {
        return Util.getLocation(n, key, Comparator.naturalOrder(), false);
    }

    @Test
    public void testGetLocationNonUniqueKeys() {
        Node<Integer, Integer> n = create(1, 1, 3, 3);
        assertEquals(0, getLocation(n, 0));
        assertEquals(2, getLocation(n, 1));
        assertEquals(2, getLocation(n, 2));
        assertEquals(4, getLocation(n, 3));
        assertEquals(4, getLocation(n, 4));
    }

    private Node<Integer, Integer> create(int... keys) {
        return new Node<Integer, Integer>() {

            @Override
            public Options<Integer, Integer> options() {
                return null;
            }

            @Override
            public Factory<Integer, Integer> factory() {
                return null;
            }

            @Override
            public int numKeys() {
                return keys.length;
            }

            @Override
            public Integer key(int i) {
                return keys[i];
            }

            @Override
            public int getLocation(Integer key) {
                return 0;
            }

            @Override
            public Split<Integer, Integer> insert(Integer key, Integer value) {
                return null;
            }

        };

    }

}
