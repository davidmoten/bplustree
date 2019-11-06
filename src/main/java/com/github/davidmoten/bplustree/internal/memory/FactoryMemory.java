package com.github.davidmoten.bplustree.internal.memory;

import com.github.davidmoten.bplustree.Factory;
import com.github.davidmoten.bplustree.Leaf;
import com.github.davidmoten.bplustree.NonLeaf;
import com.github.davidmoten.bplustree.Options;

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
}
