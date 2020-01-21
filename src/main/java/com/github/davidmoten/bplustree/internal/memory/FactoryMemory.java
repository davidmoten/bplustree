package com.github.davidmoten.bplustree.internal.memory;

import com.github.davidmoten.bplustree.internal.Factory;
import com.github.davidmoten.bplustree.internal.Leaf;
import com.github.davidmoten.bplustree.internal.Node;
import com.github.davidmoten.bplustree.internal.NonLeaf;
import com.github.davidmoten.bplustree.internal.Options;

public final class FactoryMemory<K, V> implements Factory<K, V> {

    private final Options<K, V> options;

    public FactoryMemory(Options<K, V> options) {
        this.options = options;
    }

    @Override
    public Leaf<K, V> createLeaf() {
        return new LeafMemory<K, V>(options, this);
    }

    @Override
    public NonLeaf<K, V> createNonLeaf() {
        return new NonLeafMemory<K, V>(options, this);
    }

    @Override
    public void close() throws Exception {
        // do nothing
    }

    @Override
    public void commit() {
        // do nothing
    }

    @Override
    public void root(Node<K, V> node) {
        // do nothing
    }

    @Override
    public Node<K, V> loadOrCreateRoot() {
        return createLeaf();
    }

    @Override
    public Options<K, V> options() {
        return options;
    }

}
