package com.github.davidmoten.bplustree.internal.file;

import java.util.Arrays;

import com.github.davidmoten.bplustree.internal.Factory;
import com.github.davidmoten.bplustree.internal.Leaf;
import com.github.davidmoten.bplustree.internal.Options;
import com.github.davidmoten.guavamini.Preconditions;

public final class LeafFileCached<K, V> implements Leaf<K, V>, NodeFile {

    private final FactoryFile<K, V> factory;

    private final K[] keys;
    private final V[] values;

    private final boolean[] keyChanged;
    private final boolean[] valueChanged;

    private int numKeys;
    private boolean numKeysChanged;
    private boolean numKeysLoaded;
    private long next;

    private boolean nextChanged;

    private boolean nextLoaded;

    private long position;

    @SuppressWarnings("unchecked")
    public LeafFileCached(FactoryFile<K, V> factory, long position) {
        this.keys = (K[]) new Object[factory.options().maxLeafKeys()];
        this.values = (V[]) new Object[factory.options().maxLeafKeys()];
        this.keyChanged = new boolean[factory.options().maxLeafKeys()];
        this.valueChanged = new boolean[factory.options().maxLeafKeys()];
        this.factory = factory;
        this.position = position;
        this.next = 0;
    }

    @Override
    public V value(int index) {
        V v = values[index];
        if (v == null) {
            v = factory.leafValue(position, index);
            values[index] = v;
        }
        return v;
    }

    @Override
    public K key(int index) {
        K k = keys[index];
        if (k == null) {
            k = factory.leafKey(position, index);
            keys[index] = k;
        }
        return k;
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
        LeafFileCached<K, V> o = ((LeafFileCached<K, V>) other);
        other.setNumKeys(length);
        for (int i = start; i < start + length; i++) {
            // ensure keys and values are loaded
            key(i);
            value(i);
        }
        System.arraycopy(keys, start, o.keys, 0, length);
        System.arraycopy(values, start, o.values, 0, length);
        setNumKeys(start);
        for (int i = 0; i < length; i++) {
            o.keyChanged[i] = true;
            o.valueChanged[i] = true;
        }
        for (int i = start; i < start + length; i++) {
            // don't write changes for the moved records
            keyChanged[i] = false;
            valueChanged[i] = false;
        }
    }

    @Override
    public void setNumKeys(int numKeys) {
        this.numKeys = numKeys;
        this.numKeysChanged = true;
        this.numKeysLoaded = true;
    }

    @Override
    public void setValue(int idx, V value) {
        values[idx] = value;
        valueChanged[idx] = true;
    }

    @Override
    public void insert(int idx, K key, V value) {
        // load numKeys
        numKeys();
        for (int i = idx; i < numKeys; i++) {
            // ensure keys and values are loaded
            key(i);
            value(i);
        }
        System.arraycopy(keys, idx, keys, idx + 1, numKeys - idx);
        System.arraycopy(values, idx, values, idx + 1, numKeys - idx);
        keys[idx] = key;
        values[idx] = value;
        setNumKeys(numKeys + 1);
        for (int i = idx; i < numKeys; i++) {
            keyChanged[i] = true;
            valueChanged[i] = true;
        }
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
        if (sibling == null) {
            this.next = FactoryFile.POSITION_NOT_PRESENT;
        } else {
            this.next = ((LeafFileCached<K, V>) sibling).position();
            Preconditions.checkArgument(this.position != next);
        }
        this.nextChanged = true;
        this.nextLoaded = true;
    }

    @Override
    public LeafFileCached<K, V> next() {
        if (!nextLoaded) {
            next = factory.leafNextPosition(position);
            nextLoaded = true;
        }
        if (next == FactoryFile.POSITION_NOT_PRESENT) {
            return null;
        } else {
            return factory.getLeaf(next);
        }
    }

    public long position() {
        return position;
    }

    public void position(long position) {
        // make sure you commit before calling this one though!
        this.position = position;
        reset();
    }

    private void reset() {
        resetChanged();
        resetLoaded();
    }

    private void resetLoaded() {
        numKeysLoaded = false;
        nextLoaded = false;
        next = FactoryFile.POSITION_NOT_PRESENT;
    }

    private void resetChanged() {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = null;
            values[i] = null;
            keyChanged[i] = false;
            valueChanged[i] = false;
        }
        numKeysChanged = false;
        nextChanged = false;
    }

    public void commit() {
        System.out.println("precommit: "+ this);
        if (numKeysChanged) {
            factory.leafSetNumKeys(position, numKeys);
            numKeysChanged = false;
        }
        for (int i = 0; i < keys.length; i++) {
            if (keyChanged[i]) {
                factory.leafSetKey(position, i, keys[i]);
                keyChanged[i] = false;
            }
            if (valueChanged[i]) {
                factory.leafSetValue(position, i, values[i]);
                valueChanged[i] = false;
            }
        }
        if (nextChanged) {
            factory.leafSetNext(position, next);
            nextChanged = false;
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("LeafFileCached [");
        b.append(", keys=");
        b.append(Arrays.toString(keys));
        b.append(", values=");
        b.append(Arrays.toString(values));
        b.append(", keyChanged=");
        b.append(Arrays.toString(keyChanged));
        b.append(", valueChanged=");
        b.append(Arrays.toString(valueChanged));
        b.append(", numKeys=");
        b.append(numKeys);
        b.append(", numKeysChanged=");
        b.append(numKeysChanged);
        b.append(", numKeysLoaded=");
        b.append(numKeysLoaded);
        b.append(", next=");
        b.append(next);
        b.append(", nextChanged=");
        b.append(nextChanged);
        b.append(", nextLoaded=");
        b.append(nextLoaded);
        b.append(", position=");
        b.append(position);
        b.append("]");
        return b.toString();
    }

}
