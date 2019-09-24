package logss.btree;

public interface LeafStorage<K, V> {
    
    V value(int index);
    
    K key(int index);
    
    int numKeys();
    
}
