package com.github.davidmoten.bplustree.internal;

public interface NonLeaf<K, V> extends Node<K, V> {

    public static final int TYPE = 1;

    void setNumKeys(int numKeys);

    @Override
    int numKeys();

    void setChild(int i, Node<K, V> node);

    Node<K, V> child(int i);

    @Override
    K key(int i);

    void setKey(int i, K key);

    void move(int mid, NonLeaf<K, V> other, int length);

    /**
     * Inserts the key at the given index and sets the left child of that key to be
     * {@code left}. Also increments the number of keys in the node.
     * 
     * @param i    index to insert at
     * @param key  key to insert
     * @param left child to set of the new key
     */
    void insert(int i, K key, Node<K, V> left);

    @Override
    default Split<K, V> insert(K key, V value) {
        if (numKeys() == options().maxNonLeafKeys()) { // Split
            if (Integer.valueOf(0) == key && Integer.valueOf(7) == value) {
                System.out.println("debug point");
            }
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

    /**
     * Returns the position where 'key' should be inserted in a non-leaf node that
     * has the given keys.  The position returned will be the first key K for which
     * {@code key} &lt; K.. If no key satisfies that criterion then returns the
     * current number of keys.
     * 
     * @param key key to insert
     * @return the position where key should be inserted
     */
    default int getLocation(K key) {
        return Util.getLocation(this, key, options().comparator(), false);
    }

}