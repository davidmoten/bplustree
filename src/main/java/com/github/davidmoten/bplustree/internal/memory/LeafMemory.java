package com.github.davidmoten.bplustree.internal.memory;

import java.util.Arrays;

import com.github.davidmoten.bplustree.internal.Factory;
import com.github.davidmoten.bplustree.internal.Leaf;
import com.github.davidmoten.bplustree.internal.Options;

public final class LeafMemory<K, V> extends AbstractLeafMemory<K, V> {

    private Leaf<K, V> next;

    public LeafMemory(Options<K, V> options, Factory<K, V> factory) {
        super(options, factory);
    }

    @Override
    public void setNext(Leaf<K, V> next) {
        this.next = next;
    }

    @Override
    public Leaf<K, V> next() {
        return next;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LeafMemory [");
        builder.append("numKeys=");
        builder.append(numKeys);
        builder.append(", keys=");
        builder.append(Arrays.toString(keys));
        builder.append(", values=");
        builder.append(Arrays.toString(values));
        builder.append("]");
        return builder.toString();
    }

}
