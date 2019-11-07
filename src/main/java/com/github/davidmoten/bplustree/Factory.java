package com.github.davidmoten.bplustree;

public interface Factory<K, V> extends AutoCloseable {

    Leaf<K, V> createLeaf();

    NonLeaf<K, V> createNonLeaf();

    void commit();

    /**
     * Called when the root node of the BPlusTree is initialized or changes.
     * 
     * @param node
     *            new root node
     */
    void root(Node<K, V> node);

    Node<K, V> loadOrCreateRoot();

}