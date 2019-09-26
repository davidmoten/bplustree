package logss.btree;

import java.util.ArrayList;
import java.util.List;

interface Node<K, V> {

    int getLocation(K key);

    // returns null if no split, otherwise returns split info
    Split<K, V> insert(K key, V value);

    void dump();

    K key(int i);

    int numKeys();

    default List<K> keys() {
        List<K> list = new ArrayList<K>();
        for (int i = 0; i < numKeys(); i++) {
            list.add(key(i));
        }
        return list;
    }

}