package com.github.davidmoten.bplustree.internal;

public interface Leaf<K, V> extends Node<K, V> {

    public static final int TYPE = 0;

    @Override
    Options<K, V> options();

    V value(int i);

    void setNumKeys(int numKeys);

    void setValue(int i, V value);

    /**
     * Inserts a key and value at the given index in the node and increments the
     * number of keys in the node.
     * 
     * @param i     which position to make the insertino at in the Leaf keys
     * @param key   key to insert
     * @param value value to insert
     */
    void insert(int i, K key, V value);

    /**
     * Copies length KeyValues from index start to the start of {@code newLeaf},
     * sets the number of keys in the new Leaf to be {@code length}, sets the number
     * of keys in source Leaf to be {@code start}.
     * 
     * @param start   start index of Key Value pairs to copy in current Leaf
     * @param length  number of Key Value pairs to copy
     * @param newLeaf a new empty Leaf
     */
    void move(int start, int length, Leaf<K, V> newLeaf);

    void setNext(Leaf<K, V> sibling);

    Leaf<K, V> next();

    @Override
    default Split<K, V> insert(K key, V value) {
        // Simple linear search
        int i = getLocation(key);
        int numKeys = numKeys();
        if (numKeys == options().maxLeafKeys()) {
            // The node is full. We must split it
            // the first mid entries will be retained
            // and the rest moved to a new right sibling
            int mid = (options().maxLeafKeys() + 1) / 2;
            int len = numKeys - mid;
            Leaf<K, V> sibling = factory().createLeaf();
            move(mid, len, sibling);
            if (i < mid) {
                // Inserted element goes to left sibling
                Util.insertNonfull(this, key, value, i, mid);
            } else {
                // Inserted element goes to right sibling
                // TODO this probably brings about another array copy
                // (shift to the right) in sibling so perhaps should be combined with the
                // original move
                Util.insertNonfull(sibling, key, value, i - mid, len);
            }
            sibling.setNext(next());
            setNext(sibling);
            // Notify the parent about the split
            return new Split<>(sibling.key(0), // make the right's key >=
                                               // result.key
                    this, sibling);
        } else {
            // The node was not full
            Util.insertNonfull(this, key, value, i, numKeys);
            return null;
        }
    }

    /**
     * Returns the position where 'key' should be inserted in a leaf node that has
     * the given keys.
     * 
     * @param key key to insert
     * @return the position where key should be inserted
     */
    default int getLocation(K key) {
        int numKeys = numKeys();
        for (int i = 0; i < numKeys; i++) {
            if (options().comparator().compare(key, key(i)) <= 0) {
                return i;
            }
        }
        return numKeys;
    }

}