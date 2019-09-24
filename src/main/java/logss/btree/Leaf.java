package logss.btree;

@SuppressWarnings("unchecked")
final class Leaf<K, V> implements Node<K, V> {
    private final Options<K, V> options;
    private final V[] values;
    private final K[] keys;
    private int numKeys; // number of keys
    private final LeafStore<K, V> store;

    Leaf(Options<K, V> options) {
        this.options = options;
        this.values = (V[]) new Object[options.maxLeafKeys];
        this.keys = (K[]) new Object[options.maxLeafKeys];
        this.store = options.storage.createLeafStore();
    }

    V value(int index) {
        return values[index];
    }

    K key(int index) {
        return keys[index];
    }

    int numKeys() {
        return numKeys;
    }

    /**
     * Returns the position where 'key' should be inserted in a leaf node that has
     * the given keys.
     */
    @Override
    public int getLocation(K key) {
        // Simple linear search. Faster for small values of N or M, binary search would
        // be faster for larger M / N
        for (int i = 0; i < numKeys; i++) {
            if (options.comparator.compare(keys[i], key) >= 0) {
                return i;
            }
        }
        return numKeys;
    }

    @Override
    public Split<K, V> insert(K key, V value) {
        // Simple linear search
        int i = getLocation(key);
        if (this.numKeys == options.maxLeafKeys) { // The node was full. We must split it
            int mid = (options.maxLeafKeys + 1) / 2;
            int sNum = this.numKeys - mid;
            Leaf<K, V> sibling = new Leaf<K, V>(options);
            sibling.numKeys = sNum;
            System.arraycopy(this.keys, mid, sibling.keys, 0, sNum);
            System.arraycopy(this.values, mid, sibling.values, 0, sNum);
            this.numKeys = mid;
            if (i < mid) {
                // Inserted element goes to left sibling
                this.insertNonfull(key, value, i);
            } else {
                // Inserted element goes to right sibling
                sibling.insertNonfull(key, value, i - mid);
            }
            // Notify the parent about the split
            Split<K, V> result = new Split<>(sibling.keys[0], // make the right's key >=
                                                              // result.key
                    this, sibling);
            return result;
        } else {
            // The node was not full
            this.insertNonfull(key, value, i);
            return null;
        }
    }

    private void insertNonfull(K key, V value, int idx) {
        if (idx < numKeys && keys[idx].equals(key)) {
            // We are inserting a duplicate value, simply overwrite the old one
            values[idx] = value;
        } else {
            // The key we are inserting is unique
            System.arraycopy(keys, idx, keys, idx + 1, numKeys - idx);
            System.arraycopy(values, idx, values, idx + 1, numKeys - idx);

            keys[idx] = key;
            values[idx] = value;
            numKeys++;
        }
    }

    @Override
    public void dump() {
        System.out.println("lNode h==0");
        for (int i = 0; i < numKeys; i++) {
            System.out.println(keys[i]);
        }
    }
}