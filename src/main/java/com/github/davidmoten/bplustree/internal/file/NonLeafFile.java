package com.github.davidmoten.bplustree.internal.file;

import com.github.davidmoten.bplustree.internal.Factory;
import com.github.davidmoten.bplustree.internal.Node;
import com.github.davidmoten.bplustree.internal.NonLeaf;
import com.github.davidmoten.bplustree.internal.Options;

public final class NonLeafFile<K, V> implements NonLeaf<K, V>, NodeFile {

    private final Options<K, V> options;
    private final FactoryFile<K, V> factory;
    private final long position;

    public NonLeafFile(Options<K, V> options, FactoryFile<K, V> factory, long position) {
        this.options = options;
        this.factory = factory;
        this.position = position;
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
    public void setNumKeys(int numKeys) {
        factory.nonLeafSetNumKeys(position, numKeys);
    }

    @Override
    public int numKeys() {
        return factory.nonLeafNumKeys(position);
    }

    @Override
    public void setChild(int index, Node<K, V> node) {
        factory.nonLeafSetChild(position, index, (NodeFile) node);
    }

    @Override
    public Node<K, V> child(int index) {
        return factory.nonLeafChild(position, index);
    }

    @Override
    public K key(int index) {
        return factory.nonLeafKey(position, index);
    }

    @Override
    public void setKey(int index, K key) {
        factory.nonLeafSetKey(position, index, key);
    }

    @Override
    public void move(int mid, NonLeaf<K, V> other, int length) {
        factory.nonLeafMove(position, mid, length, (NonLeafFile<K, V>) other);

    }

    @Override
    public void insert(int idx, K key, Node<K, V> left) {
        factory.nonLeafInsert(position, idx, key, (NodeFile) left);
    }

    @Override
    public long position() {
        return position;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("NonLeafFile [");
        b.append("position=");
        b.append(position);
        b.append(", numKeys=");
        b.append(numKeys());
        b.append(", keys=[");
        StringBuilder b2 = new StringBuilder();
        int n = numKeys();
        for (int i = 0; i < n; i++) {
            if (b2.length() > 0) {
                b2.append(", ");
            }
            b2.append(key(i));
        }
        b.append(b2.toString());
        b.append("]");
        b.append("]");
        return b.toString();
    }

}
