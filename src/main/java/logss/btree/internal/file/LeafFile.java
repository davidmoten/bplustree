package logss.btree.internal.file;

import logss.btree.Factory;
import logss.btree.Leaf;
import logss.btree.Options;

public class LeafFile<K, V> implements Leaf<K, V> {

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
        return factory.key(position, i);
    }

    @Override
    public int numKeys() {
        return factory.numKeys(position);
    }

    @Override
    public Factory<K, V> factory() {
        return factory;
    }

    @Override
    public Options<K, V> options() {
        return options;
    }

    @Override
    public V value(int index) {
        return factory.value(position, index);
    }

    @Override
    public void setNumKeys(int numKeys) {
        factory.setNumKeys(position, numKeys);
    }

    @Override
    public void setValue(int idx, V value) {
        factory.setValue(position, idx, value);
    }

    @Override
    public void insert(int idx, K key, V value) {
        factory.insert(position, idx, key, value);
    }

    @Override
    public void move(int start, int length, Leaf<K, V> other) {
        factory.move(position, start, length, (LeafFile<K, V>) other);
    }

    @Override
    public void setNext(Leaf<K, V> sibling) {
        factory.setNext(position, (LeafFile<K, V>) sibling);
    }

    @Override
    public Leaf<K, V> next() {
        return factory.next(position);
    }

    public long position() {
        return position;
    }

}
