package logss.btree;

public interface NonLeafStore<K,V> {
    
    void setNumKeys(int numKeys);
    
    int numKeys();

    void setChild(int index, Node<K, V> node);
    
    Node<K,V> child(int index);

    K key(int index);

    void setKey(int index, K key);

    void move(int mid, NonLeaf<K, V> other, int length);

    void insert(int idx, K key, Node<K, V> left);
    
}
