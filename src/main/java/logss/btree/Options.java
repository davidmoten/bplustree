package logss.btree;

import java.util.Comparator;

final class Options<K, V> {

    /** the maximum number of keys in the leaf node, M must be > 0 */
    final int maxLeafKeys;

    /**
     * the maximum number of keys in inner node, the number of pointer is N+1, N
     * must be > 2
     */
    final int maxNonLeafKeys;
    final Comparator<? super K> comparator;
    final Storage<K, V> storage;

    final boolean uniqueKeys;

    Options(int maxLeafKeys, int maxInnerKeys, boolean uniqueKeys, Comparator<? super K> comparator,
            Storage<K, V> storage) {
        this.maxLeafKeys = maxLeafKeys;
        this.maxNonLeafKeys = maxInnerKeys;
        this.comparator = comparator;
        this.storage = storage;
        this.uniqueKeys = uniqueKeys;
    }

}