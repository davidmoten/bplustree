package com.github.davidmoten.bplustree.internal.file;

import com.github.davidmoten.bplustree.internal.Factory;
import com.github.davidmoten.bplustree.internal.Leaf;
import com.github.davidmoten.bplustree.internal.Options;

public final class LeafFileCached<K, V> implements Leaf<K, V>, NodeFile {

    private final FactoryFile<K, V> factory;

    protected final K[] keys;
    protected final V[] values;

    private int numKeys;
    private boolean numKeysChanged;
    private boolean numKeysLoaded;
    private long next;
    private long position;

    @SuppressWarnings("unchecked")
    public LeafFileCached(FactoryFile<K, V> factory, long position) {
        this.keys = (K[]) new Object[factory.options().maxLeafKeys()];
        this.values = (V[]) new Object[factory.options().maxLeafKeys()];
        this.factory = factory;
        this.position = position;
    }

    @Override
    public V value(int index) {
        V v = values[index];
        if (v == null) {
            return factory.leafValue(position, index);
        } else {
            return v;
        }
    }

    @Override
    public K key(int index) {
        K k = keys[index];
        if (k == null) {
            return factory.leafKey(position, index);
        } else {
            return k;
        }
    }

    @Override
    public int numKeys() {
        if (numKeysLoaded) {
            return numKeys;
        } else {
            numKeysLoaded = true;
            return numKeys = factory.leafNumKeys(position);
        }
    }

    @Override
    public void move(int start, int length, Leaf<K, V> other) {
        other.setNumKeys(length);
        System.arraycopy(keys, start, ((LeafFileCached<K, V>) other).keys, 0, length);
        System.arraycopy(values, start, ((LeafFileCached<K, V>) other).values, 0, length);
        setNumKeys(start);
    }

    @Override
    public void setNumKeys(int numKeys) {
        this.numKeys = numKeys;
        this.numKeysChanged = true;
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
        setNumKeys(numKeys() + 1);
    }

    @Override
    public Options<K, V> options() {
        return factory.options();
    }

    @Override
    public Factory<K, V> factory() {
        return factory;
    }

    @Override
    public void setNext(Leaf<K, V> sibling) {
        this.next = ((LeafFileCached<K, V>) sibling).position();
    }

    @Override
    public Leaf<K, V> next() {
        return ((FactoryFile<K, V>) factory).getLeaf(next);
    }

    public long position() {
        return position;
    }

    public void position(long position) {
        // make sure you commit before calling this one though!
        this.position = position;
    }

    public void commit() {
        if (numKeysChanged) {
            factory.leafSetNumKeys(position, numKeys);
        }
        for (int i = 0; i < keys.length; i++) {
            K k = keys[i];
            if (k != null) {
                factory.leafSetKey(position, i, k);
            }
            V v = values[i];
            if (v != null) {
                factory.leafSetValue(position, i, v);
            }
        }
    }

}
