package logss.btree;

public interface Leaf<K, V> extends Node<K, V> {

    Options<K, V> options();

    V value(int i);

    void setNumKeys(int numKeys);

    void setValue(int i, V value);

    /**
     * Inserts a key and value at the given index in the node and increments the
     * number of keys in the node.
     * 
     * @param i
     * @param key
     * @param value
     */
    void insert(int i, K key, V value);

    /**
     * Copies length KeyValues from index start to the start of {@code newLeaf},
     * sets the number of keys in the new Leaf to be {@code length}, sets the number
     * of keys in source Leaf to be {@code start}.
     * 
     * @param start
     *            start index of Key Value pairs to copy in current Leaf
     * @param length
     *            number of Key Value pairs to copy
     * @param newLeaf
     *            a new empty Leaf
     */
    void move(int start, int length, Leaf<K, V> newLeaf);

    void setNext(Leaf<K, V> sibling);

    Leaf<K, V> next();

    @Override
    default Split<K, V> insert(K key, V value) {
        // Simple linear search
        int i = getLocation(key);
        if (numKeys() == options().maxLeafKeys()) { // The node was full. We must split it
            int mid = (options().maxLeafKeys() + 1) / 2;
            int len = numKeys() - mid;
            Leaf<K, V> sibling = factory().createLeaf();
            move(mid, len, sibling);
            // System.arraycopy(this.keys, mid, sibling.keys, 0, sNum);
            // System.arraycopy(this.values, mid, sibling.values, 0, sNum);
            setNumKeys(mid);
            if (i < mid) {
                // Inserted element goes to left sibling
                Util.insertNonfull(this, key, value, i);
            } else {
                // Inserted element goes to right sibling
                Util.insertNonfull(sibling, key, value, i - mid);
            }
            setNext(sibling);
            // Notify the parent about the split
            return new Split<>(sibling.key(0), // make the right's key >=
                                               // result.key
                    this, sibling);
        } else {
            // The node was not full
            Util.insertNonfull(this, key, value, i);
            return null;
        }
    }

    /**
     * Returns the position where 'key' should be inserted in a leaf node that has
     * the given keys.
     */
    default int getLocation(K key) {
        // Simple linear search. Faster for small values of N or M, binary search would
        // be faster for larger M / N
        int numKeys = numKeys();
        for (int i = 0; i < numKeys; i++) {
            if (options().comparator().compare(key(i), key) >= 0) {
                return i;
            }
        }
        return numKeys;
    }

}