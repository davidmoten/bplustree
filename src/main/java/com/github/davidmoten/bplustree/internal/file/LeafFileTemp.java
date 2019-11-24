package com.github.davidmoten.bplustree.internal.file;

import com.github.davidmoten.bplustree.internal.Leaf;
import com.github.davidmoten.bplustree.internal.Options;
import com.github.davidmoten.bplustree.internal.memory.AbstractLeafMemory;

public class LeafFileTemp<K, V> extends AbstractLeafMemory<K, V> {

    private long next;

    public LeafFileTemp(Options<K, V> options, FactoryFile<K, V> factory) {
        super(options, factory);
    }

    @Override
    public void setNext(Leaf<K, V> sibling) {
        this.next = ((LeafFile<K, V>) sibling).position();
    }

    @Override
    public Leaf<K, V> next() {
        return ((FactoryFile<K, V>) factory).getLeaf(next);
    }

}
