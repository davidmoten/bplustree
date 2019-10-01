package logss.btree.internal.memory;

import logss.btree.Factory;
import logss.btree.Leaf;
import logss.btree.Options;

public final class LeafMemory<K, V> implements Leaf<K, V> {

    private final Options<K, V> options;
    private final Factory<K, V> factory;
    private final K[] keys;
    private final V[] values;
    private int numKeys;
    private Leaf<K, V> next;

    @SuppressWarnings("unchecked")
    public LeafMemory(Options<K,V> options, Factory<K,V> factory) {
        this.options = options;
        keys = (K[]) new Object[options.maxLeafKeys()];
        values = (V[]) new Object[options.maxLeafKeys()];
        this.factory = factory;
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
    public void move(int start, Leaf<K, V> other, int length) {
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
    public void setNext(Leaf<K, V> next) {
        this.next = next;
    }

    @Override
    public Leaf<K, V> next() {
       return next;
    }

    @Override
    public Options<K, V> options() {
        return options;
    }

    @Override
    public Factory<K, V> factory() {
        return factory;
    }

}
