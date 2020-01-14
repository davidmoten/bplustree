package com.github.davidmoten.bplustree.internal.file;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.github.davidmoten.bplustree.internal.Lockable;

// Position -> NodeFile, Counter -> Position
final class ExpiringCache<K, V extends Lockable> {

    private final int maxSize;
    private final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();

    ExpiringCache(int maxSize) {
        this.maxSize = maxSize;
    }

    void put(K key, V value) {
        map.put(key, value);
    }

    V expireOne() {
        Iterator<Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<K, V> entry = it.next();
            if (!entry.getValue().isLocked()) {
                return map.remove(entry.getKey());
            }
        }
        throw new RuntimeException("all entries locked, cannot expire one");
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
