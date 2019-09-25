package logss.btree;

public interface LeafStore<K, V> {
    
    V value(int index);
    
    K key(int index);
    
    int numKeys();

    void setNumKeys(int numKeys);

    void setValue(int idx, V value);

    void insert(int idx, K key, V value);

    void move(int start, Leaf<K, V> other, int length);
    
}
