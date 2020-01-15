package com.github.davidmoten.bplustree.internal.file;

import com.github.davidmoten.bplustree.internal.Leaf;
import com.github.davidmoten.bplustree.internal.Options;

public class LeafFile<K, V> implements Leaf<K, V>, NodeFile {

    private final FactoryFile<K, V> factory;
    private long position;

    public LeafFile(FactoryFile<K, V> factory, long position) {
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
        return factory.options();
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
    public LeafFile<K, V> next() {
        return factory.leafNext(position);
    }

    @Override
    public long position() {
        return position;
    }
    
    @Override
    public void position(long position) {
        this.position = position;
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("LeafFile [");
        b.append("position=");
        b.append(position);
        b.append(", numKeys=");
        b.append(numKeys());
        b.append(", keyValues=[");
        StringBuilder b2 = new StringBuilder();
        int n = numKeys();
        for (int i = 0; i < n; i++) {
            if (b2.length() > 0) {
                b2.append(", ");
            }
            b2.append(key(i));
            b2.append("->");
            b2.append(value(i));
        }
        b.append(b2.toString());
        b.append("]");
        b.append("]");
        return b.toString();
    }

}
