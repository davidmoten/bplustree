package logss.btree.internal.file;

import logss.btree.Factory;
import logss.btree.Leaf;
import logss.btree.Options;

public class LeafFile<K, V> implements Leaf<K, V> {

    @Override
    public K key(int i) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int numKeys() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Factory<K, V> factory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Options<K, V> options() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public V value(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setNumKeys(int numKeys) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setValue(int idx, V value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void insert(int idx, K key, V value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void move(int start, Leaf<K, V> other, int length) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNext(Leaf<K, V> sibling) {
        // TODO Auto-generated method stub

    }

    @Override
    public Leaf<K, V> next() {
        // TODO Auto-generated method stub
        return null;
    }

}
