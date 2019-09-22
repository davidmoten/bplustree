package logss.btree;

import java.util.Comparator;

final class Options<K> {

    /** the maximum number of keys in the leaf node, M must be > 0 */
    final int maxLeafKeys;

    /**
     * the maximum number of keys in inner node, the number of pointer is N+1, N
     * must be > 2
     */
    final int maxInnerKeys;
    final Comparator<? super K> comparator;

    Options(int maxLeafKeys, int maxInnerKeys, Comparator<? super K> comparator) {
        this.maxLeafKeys = maxLeafKeys;
        this.maxInnerKeys = maxInnerKeys;
        this.comparator = comparator;
    }

}