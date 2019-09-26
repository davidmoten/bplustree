package logss.btree;

public final class Leaf<K, V> implements Node<K, V> {
    private final Options<K, V> options;
    final LeafStore<K, V> store;
    private Leaf<K, V> next;

    Leaf(Options<K, V> options) {
        this.options = options;
        this.store = options.storage.createLeafStore();
    }

    V value(int index) {
        return store.value(index);
    }

    @Override
    public K key(int index) {
        return store.key(index);
    }

    @Override
    public int numKeys() {
        return store.numKeys();
    }

    /**
     * Returns the position where 'key' should be inserted in a leaf node that has
     * the given keys.
     */
    @Override
    public int getLocation(K key) {
        // Simple linear search. Faster for small values of N or M, binary search would
        // be faster for larger M / N
        int numKeys = store.numKeys();
        for (int i = 0; i < numKeys; i++) {
            if (options.comparator.compare(store.key(i), key) >= 0) {
                return i;
            }
        }
        return numKeys;
    }

    @Override
    public Split<K, V> insert(K key, V value) {
        // Simple linear search
        int i = getLocation(key);
        if (store.numKeys() == options.maxLeafKeys) { // The node was full. We must split it
            int mid = (options.maxLeafKeys + 1) / 2;
            int len = store.numKeys() - mid;
            Leaf<K, V> sibling = new Leaf<K, V>(options);
            store.move(mid, sibling, len);
            // System.arraycopy(this.keys, mid, sibling.keys, 0, sNum);
            // System.arraycopy(this.values, mid, sibling.values, 0, sNum);
            store.setNumKeys(mid);
            if (i < mid) {
                // Inserted element goes to left sibling
                insertNonfull(key, value, i);
            } else {
                // Inserted element goes to right sibling
                sibling.insertNonfull(key, value, i - mid);
            }
            this.next = sibling;
            // Notify the parent about the split
            return new Split<>(sibling.store.key(0), // make the right's key >=
                                                     // result.key
                    this, sibling);
        } else {
            // The node was not full
            insertNonfull(key, value, i);
            return null;
        }
    }

    void insertNonfull(K key, V value, int idx) {
        if (idx < store.numKeys() && store.key(idx).equals(key)) {
            // We are inserting a duplicate value, simply overwrite the old one
            store.setValue(idx, value);
        } else {
            // The key we are inserting is unique
            store.insert(idx, key, value);
        }
    }
    
    Leaf<K,V> next() {
        return next;
    }

}