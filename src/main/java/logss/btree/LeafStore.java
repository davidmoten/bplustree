package logss.btree;

public interface LeafStore<K, V> {
    
    V value(int index);
    
    K key(int index);
    
    int numKeys();
    
}
