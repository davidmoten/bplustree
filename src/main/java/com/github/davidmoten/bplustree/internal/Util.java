package com.github.davidmoten.bplustree.internal;

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
            // TODO put at end of duplicate keys?
            // so that iteration reflects sort order then addition order
            // The key we are inserting is unique
            leaf.insert(idx, key, value);
        }
    }

    static <K, V> void insertNonfull(NonLeaf<K, V> node, K key, V value) {
        // Simple linear search
        int index = node.getLocation(key);
        Split<K, V> result = node.child(index).insert(key, value);

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

}
