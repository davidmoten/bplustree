package logss.btree.internal;

import java.util.Comparator;

import logss.btree.FactoryProvider;

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

    public Options(int maxLeafKeys, int maxInnerKeys, boolean uniqueKeys, Comparator<? super K> comparator,
            FactoryProvider<K, V> factoryProvider) {
        this.maxLeafKeys = maxLeafKeys;
        this.maxNonLeafKeys = maxInnerKeys;
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