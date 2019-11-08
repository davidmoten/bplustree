package com.github.davidmoten.bplustree.internal;

import java.util.Comparator;

import com.github.davidmoten.guavamini.Preconditions;

public final class Options<K, V> {

    /** the maximum number of keys in the leaf node, M must be > 0 */
    private final int maxLeafKeys;

    /**
     * the maximum number of keys in inner node, the number of pointer is N+1, N
     * must be > 2
     */
    private final int maxNonLeafKeys;
    private final Comparator<? super K> comparator;
    private final boolean uniqueKeys;
    private final FactoryProvider<K, V> factoryProvider;

    public Options(int maxLeafKeys, int maxNonLeafKeys, boolean uniqueKeys,
            Comparator<? super K> comparator, FactoryProvider<K, V> factoryProvider) {
        // only one byte used to store num keys so check values
        Preconditions.checkArgument(0 < maxLeafKeys && maxLeafKeys <= 255);
        Preconditions.checkArgument(0 < maxNonLeafKeys && maxNonLeafKeys <= 255);
        this.maxLeafKeys = maxLeafKeys;
        this.maxNonLeafKeys = maxNonLeafKeys;
        this.comparator = comparator;
        this.uniqueKeys = uniqueKeys;
        this.factoryProvider = factoryProvider;
    }

    public int maxLeafKeys() {
        return maxLeafKeys;
    }

    public int maxNonLeafKeys() {
        return maxNonLeafKeys;
    }

    public Comparator<? super K> comparator() {
        return comparator;
    }

    public boolean uniqueKeys() {
        return uniqueKeys;
    }

    public FactoryProvider<K, V> factoryProvider() {
        return factoryProvider;
    }

}