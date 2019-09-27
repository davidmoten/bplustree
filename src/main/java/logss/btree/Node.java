package logss.btree;

import java.util.ArrayList;
import java.util.List;

interface Node<K, V> {

    // returns null if no split, otherwise returns split info
    Split<K, V> insert(K key, V value);

    K key(int i);

    int numKeys();
    
    Options<K,V> options();

    default List<K> keys() {
        List<K> list = new ArrayList<K>();
        for (int i = 0; i < numKeys(); i++) {
            list.add(key(i));
        }
        return list;
    }
    
    /**
     * Returns the position where 'key' should be inserted in a leaf node that has
     * the given keys.
     */
    default int getLocation(K key) {
        // Simple linear search. Faster for small values of N or M, binary search would
        // be faster for larger M / N
        int numKeys = numKeys();
        for (int i = 0; i < numKeys; i++) {
            if (options().comparator.compare(key(i), key) >= 0) {
                return i;
            }
        }
        return numKeys;
    }

}