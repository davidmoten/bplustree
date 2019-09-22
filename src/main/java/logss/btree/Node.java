package logss.btree;

interface Node<K, V> {

    int getLocation(K key);

    // returns null if no split, otherwise returns split info
    Split<K, V> insert(K key, V value);

    void dump();
    
}