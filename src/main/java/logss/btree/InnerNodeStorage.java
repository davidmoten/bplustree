package logss.btree;

public interface InnerNodeStorage<K,V> {
    
    void setNumKeys(int numKeys);
    
    int numKeys();

    void setChild(int index, Node<K, V> node);
    
}
