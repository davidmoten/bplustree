package com.github.davidmoten.bplustree.internal;

import java.util.Comparator;

final class Util {

    private Util() {
        // prevent instantiation
    }

    static <K, V> void insertNonfull(Leaf<K, V> leaf, K key, V value, int idx, int numKeys) {
        // numKeys == leaf.numKeys() but might be costly to call and may have already
        // been calculated by the calling methods so we use a pased value
        if (idx < numKeys && leaf.options().uniqueKeys() && leaf.key(idx).equals(key)) {
            // We are inserting a duplicate value, simply overwrite the old one
            leaf.setValue(idx, value);
        } else {
            // The key we are inserting is unique
            leaf.insert(idx, key, value);
        }
    }

    static <K, V> void insertNonfull(NonLeaf<K, V> node, K key, V value) {
        // Simple linear search
        int index = node.getLocation(key);
        Node<K, V> child = node.child(index);
        Split<K, V> result = child.insert(key, value);

        if (result != null) {
            if (index == node.numKeys()) {
                // Insertion at the rightmost key
                node.setKey(index, result.key);
                node.setChild(index, result.left);
                node.setChild(index + 1, result.right);
                node.setNumKeys(node.numKeys() + 1);
            } else {
                // Insertion not at the rightmost key
                // shift i>idx to the right
                node.insert(index, result.key, result.left);
                node.setChild(index + 1, result.right);
            }
        } // else the current node is not affected
    }

    static <K, V> int getLocation(Node<K, V> node, K key, Comparator<? super K> comparator, boolean acceptEquals) {
        int numKeys = node.numKeys();
        if (numKeys == 0) {
            return 0;
        }
        int start = 0;
        int finish = numKeys - 1;
        while (true) {
            int mid = (start + finish) / 2;
            int c = comparator.compare(key, node.key(mid));
            if (c < 0 || (acceptEquals && c == 0)) {
                finish = mid;
                if (start == finish) {
                    return mid;
                }
            } else {
                if (start == finish) {
                    return mid + 1;
                }
                start = mid + 1;
            }
        }
    }

}
