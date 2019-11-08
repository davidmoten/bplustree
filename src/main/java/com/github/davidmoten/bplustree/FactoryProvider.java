package com.github.davidmoten.bplustree;

public interface FactoryProvider<K, V> {

    Factory<K, V> createFactory(Options<K, V> options);

}
