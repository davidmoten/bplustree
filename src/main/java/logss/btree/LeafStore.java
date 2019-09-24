package logss.btree;

public interface LeafStore<K, V> {
    
    V value(int index);
    
    K key(int index);
    
    int numKeys();

    void move(K[] keys, int mid, Leaf<K, V> sibling, int numKeys);

    void setNumKeys(int numKeys);

    void setValue(int idx, V value);
    
}
