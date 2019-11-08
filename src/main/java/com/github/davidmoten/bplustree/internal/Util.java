package com.github.davidmoten.bplustree.internal;

final class Util {

    static <K, V> void insertNonfull(Leaf<K, V> leaf, K key, V value, int idx) {
        if (idx < leaf.numKeys() && leaf.options().uniqueKeys() && leaf.key(idx).equals(key)) {
            // We are inserting a duplicate value, simply overwrite the old one
            leaf.setValue(idx, value);
        } else {
            // TODO put at end of duplicate keys?
            // so that iteration reflects sort order then addition order
            // The key we are inserting is unique
            leaf.insert(idx, key, value);
        }
    }

    
    static <K,V> void insertNonfull(NonLeaf<K,V> node, K key, V value) {
        // Simple linear search
        int index = node.getLocation(key);
        Split<K, V> result = node.child(index).insert(key, value);

        if (result != null) {
            if (index == node.numKeys()) {
                // Insertion at the rightmost key
                node.setKey(index, result.key);
                node.setChild(index, result.left);
                node.setChild(index + 1, result.right);
            } else {
                // Insertion not at the rightmost key
                // shift i>idx to the right
                node.insert(index, result.key, result.left);
                node.setChild(index + 1, result.right);
            }
            node.setNumKeys(node.numKeys() + 1);
        } // else the current node is not affected
    }

}
