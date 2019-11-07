package com.github.davidmoten.bplustree;

import com.github.davidmoten.guavamini.Preconditions;

public final class Entry<K,V> {
    
    private final K key;
    private final V value;
    
    private Entry(K key, V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        this.key = key;
        this.value = value;
    }
    
    public static <K,V> Entry<K,V> create(K key, V value){
        return new Entry<K,V>(key, value);
    }

}
