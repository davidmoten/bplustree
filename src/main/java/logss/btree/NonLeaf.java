package logss.btree;

public interface NonLeaf<K, V> extends Node<K, V> {
    
    public static final int TYPE = 1;

    void setNumKeys(int numKeys);

    int numKeys();

    void setChild(int i, Node<K, V> node);

    Node<K, V> child(int i);

    K key(int i);

    void setKey(int i, K key);

    void move(int mid, NonLeaf<K, V> other, int length);

    void insert(int i, K key, Node<K, V> left);

    @Override
    default Split<K, V> insert(K key, V value) {
        if (numKeys() == options().maxNonLeafKeys()) { // Split
            int mid = options().maxNonLeafKeys() / 2 + 1;
            int len = options().maxNonLeafKeys() - mid;
            NonLeaf<K, V> sibling = factory().createNonLeaf();
            move(mid, sibling, len);

            // Set up the return variable
            Split<K, V> result = new Split<>(key(mid - 1), this, sibling);

            // Now insert in the appropriate sibling
            if (options().comparator().compare(key, result.key) < 0) {
                Util.insertNonfull(this, key, value);
            } else {
                Util.insertNonfull(sibling, key, value);
            }
            return result;

        } else {// No split
            Util.insertNonfull(this, key, value);
            return null;
        }
    }

    // TODO move to another class
    /**
     * Returns the position where 'key' should be inserted in a leaf node that has
     * the given keys.
     */
    default int getLocation(K key) {
        // Simple linear search. Faster for small values of N or M, binary search would
        // be faster for larger M / N
        int numKeys = numKeys();
        for (int i = 0; i < numKeys; i++) {
            if (options().comparator().compare(key(i), key) > 0) {
                return i;
            }
        }
        return numKeys;
    }

}