package logss.btree;

final class Util {

    static <K, V> void insertNonfull(Leaf<K, V> leaf, K key, V value, int idx) {
        if (idx < leaf.numKeys() && leaf.options().uniqueKeys() && leaf.key(idx).equals(key)) {
            // We are inserting a duplicate value, simply overwrite the old one
            leaf.setValue(idx, value);
        } else {
            // TODO put at end of duplicate keys
            // so that iteration reflects sort order then addition order
            // The key we are inserting is unique
            leaf.insert(idx, key, value);
        }
    }

    
    static <K,V> void insertNonfull(NonLeaf<K,V> node, K key, V value) {
        // Simple linear search
        int idx = node.getLocation(key);
        Split<K, V> result = node.child(idx).insert(key, value);

        if (result != null) {
            if (idx == node.numKeys()) {
                // Insertion at the rightmost key
                node.setKey(idx, result.key);
                node.setChild(idx, result.left);
                node.setChild(idx + 1, result.right);
            } else {
                // Insertion not at the rightmost key
                // shift i>idx to the right
                node.insert(idx, result.key, result.left);
                node.setChild(idx + 1, result.right);
            }
            node.setNumKeys(node.numKeys() + 1);
        } // else the current node is not affected
    }

}
