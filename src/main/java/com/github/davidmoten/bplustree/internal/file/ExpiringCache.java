package com.github.davidmoten.bplustree.internal.file;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

// Position -> NodeFile, Counter -> Position
final class ExpiringCache<K, V> {

    private final int maxSize;
    private final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();

    ExpiringCache(int maxSize) {
        this.maxSize = maxSize;
    }

    void put(K key, V value) {
        map.put(key, value);
    }

    V expireOne() {
        Iterator<K> it = map.keySet().iterator();
        K k = it.next();
        V v = map.remove(k);
        return v;
    }

    V get(K key) {
        return map.get(key);
    }

    int size() {
        return map.size();
    }

    boolean isAtMaxSize() {
        return map.size() == maxSize;
    }

    void forEach(BiConsumer<? super K, ? super V> consumer) {
        for (Entry<K, V> entry : map.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

}
