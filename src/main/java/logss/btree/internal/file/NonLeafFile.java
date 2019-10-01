package logss.btree.internal.file;

import logss.btree.Factory;
import logss.btree.Node;
import logss.btree.NonLeaf;
import logss.btree.Options;

public class NonLeafFile<K,V> implements NonLeaf<K,V> {

    @Override
    public Options<K, V> options() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Factory<K, V> factory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setNumKeys(int numKeys) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int numKeys() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setChild(int index, Node<K, V> node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Node<K, V> child(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public K key(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setKey(int index, K key) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void move(int mid, NonLeaf<K, V> other, int length) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void insert(int idx, K key, Node<K, V> left) {
        // TODO Auto-generated method stub
        
    }

}
