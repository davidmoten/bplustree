package com.github.davidmoten.bplustree.internal.memory;

import java.util.Arrays;

import com.github.davidmoten.bplustree.internal.Factory;
import com.github.davidmoten.bplustree.internal.Leaf;
import com.github.davidmoten.bplustree.internal.Options;

public final class LeafMemory<K, V> implements Leaf<K, V> {

    private final Options<K, V> options;
    private final Factory<K, V> factory;
    final K[] keys;
    final V[] values;
    private int numKeys;
    private Leaf<K, V> next;

    @SuppressWarnings("unchecked")
    public LeafMemory(Options<K, V> options, Factory<K, V> factory) {
        this.options = options;
        this.factory = factory;
        keys = (K[]) new Object[options.maxLeafKeys()];
        values = (V[]) new Object[options.maxLeafKeys()];
    }

    @Override
    public V value(int index) {
        return values[index];
    }

    @Override
    public K key(int index) {
        return keys[index];
    }

    @Override
    public int numKeys() {
        return numKeys;
    }

    @Override
    public void move(int start, int length, Leaf<K, V> other) {
        other.setNumKeys(length);
        System.arraycopy(keys, start, ((LeafMemory<K, V>) other).keys, 0, length);
        System.arraycopy(values, start, ((LeafMemory<K, V>) other).values, 0, length);
        numKeys = start;
    }

    @Override
    public void setNumKeys(int numKeys) {
        this.numKeys = numKeys;
    }

    @Override
    public void setValue(int idx, V value) {
        values[idx] = value;
    }

    @Override
    public void insert(int idx, K key, V value) {
        System.arraycopy(keys, idx, keys, idx + 1, numKeys - idx);
        System.arraycopy(values, idx, values, idx + 1, numKeys - idx);
        keys[idx] = key;
        values[idx] = value;
        numKeys++;
    }

    @Override
    public Options<K, V> options() {
        return options;
    }

    @Override
    public Factory<K, V> factory() {
        return factory;
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

    @Override
    public void locked(boolean locked) {
        // do nothing
    }

    @Override
    public boolean isLocked() {
        return false;
    }

}
