package logss.btree;

public interface NonLeaf<K, V> extends Node<K, V> {

    void setNumKeys(int numKeys);

    int numKeys();

    void setChild(int index, Node<K, V> node);

    Node<K, V> child(int index);

    K key(int index);

    void setKey(int index, K key);

    void move(int mid, NonLeaf<K, V> other, int length);

    void insert(int idx, K key, Node<K, V> left);

    @Override
    default Split<K, V> insert(K key, V value) {
        if (numKeys() == options().maxNonLeafKeys) { // Split
            int mid = options().maxNonLeafKeys/ 2 + 1;
            int len = numKeys() - mid;
            NonLeaf<K, V> sibling = options().factory.createNonLeaf(options());
            move(mid, sibling, len);

            // Set up the return variable
            Split<K, V> result = new Split<>(key(mid - 1), this, sibling);

            // Now insert in the appropriate sibling
            if (options().comparator.compare(key, result.key) < 0) {
                insertNonfull(key, value);
            } else {
                sibling.insertNonfull(key, value);
            }
            return result;

        } else {// No split
            insertNonfull(key, value);
            return null;
        }
    }

    //TODO move to another class
    default void insertNonfull(K key, V value) {
        // Simple linear search
        int idx = getLocation(key);
        Split<K, V> result = child(idx).insert(key, value);

        if (result != null) {
            if (idx == numKeys()) {
                // Insertion at the rightmost key
                setKey(idx, result.key);
                setChild(idx, result.left);
                setChild(idx + 1, result.right);
            } else {
                // Insertion not at the rightmost key
                // shift i>idx to the right
                insert(idx, result.key, result.left);
                setChild(idx + 1, result.right);
            }
            setNumKeys(numKeys() + 1);
        } // else the current node is not affected
    }


}