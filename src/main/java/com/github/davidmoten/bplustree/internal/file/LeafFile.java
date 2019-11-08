package com.github.davidmoten.bplustree.internal.file;

import com.github.davidmoten.bplustree.internal.Leaf;
import com.github.davidmoten.bplustree.internal.Options;

public class LeafFile<K, V> implements Leaf<K, V>, NodeFile {

    private final FactoryFile<K, V> factory;
    private long position;
    private final Options<K, V> options;

    public LeafFile(Options<K, V> options, FactoryFile<K, V> factory, long position) {
        this.options = options;
        this.factory = factory;
        this.position = position;
    }

    @Override
    public K key(int i) {
        return factory.leafKey(position, i);
    }

    @Override
    public int numKeys() {
        return factory.leafNumKeys(position);
    }

    @Override
    public FactoryFile<K, V> factory() {
        return factory;
    }

    @Override
    public Options<K, V> options() {
        return options;
    }

    @Override
    public V value(int index) {
        return factory.leafValue(position, index);
    }

    @Override
    public void setNumKeys(int numKeys) {
        factory.leafSetNumKeys(position, numKeys);
    }

    @Override
    public void setValue(int idx, V value) {
        factory.leafSetValue(position, idx, value);
    }

    @Override
    public void insert(int idx, K key, V value) {
        factory.leafInsert(position, idx, key, value);
    }

    @Override
    public void move(int start, int length, Leaf<K, V> other) {
        factory.leafMove(position, start, length, (LeafFile<K, V>) other);
    }

    @Override
    public void setNext(Leaf<K, V> sibling) {
        factory.leafSetNext(position, (LeafFile<K, V>) sibling);
    }

    @Override
    public Leaf<K, V> next() {
        return factory.leafNext(position);
    }

    @Override
    public long position() {
        return position;
    }

}
