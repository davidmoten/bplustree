package logss.btree;

import java.util.Comparator;

/**
 * B+ Tree If you understand B+ or B Tree better, M & N don't need to be the
 * same Here is an example of M=N=4, with 12 keys
 * 
 * 5 / \ 3 7 9 / \ / | \ 1 2 3 4 5 6 7 8 9 10 11 12
 * 
 * @author jwang01
 * @version 1.0.0 created on May 19, 2006 edited by Spoon! 2008 edited by Mistro
 *          2010
 */
public class BPlusTree<K, V> {

    private final Options<K> options;

    /**
     * Pointer to the root node. It may be a leaf or an inner node, but it is never
     * null.
     */
    private Node<K, V> root;

    /** Create a new empty tree. */
    private BPlusTree(int maxLeafKeys, int maxInnerKeys, Comparator<? super K> comparator) {
        this.options = new Options<K>(maxLeafKeys, maxInnerKeys, comparator);
        this.root = new Leaf<K, V>(options);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private static final int NOT_SPECIFIED = -1;

        private static final int DEFAULT_NUM_KEYS = 4;

        private int maxLeafKeys = NOT_SPECIFIED;
        private int maxInnerKeys = NOT_SPECIFIED;

        Builder() {
            // prevent instantiation
        }

        public Builder maxLeafKeys(int maxLeafKeys) {
            this.maxLeafKeys = maxLeafKeys;
            return this;
        }

        public Builder maxInnerKeys(int maxInnerKeys) {
            this.maxInnerKeys = maxInnerKeys;
            return this;
        }

        public Builder maxKeys(int maxKeys) {
            maxLeafKeys(maxKeys);
            return maxInnerKeys(maxKeys);
        }

        public <K, V> BPlusTree<K, V> comparator(Comparator<? super K> comparator) {
            if (maxLeafKeys == NOT_SPECIFIED) {
                if (maxInnerKeys == NOT_SPECIFIED) {
                    maxLeafKeys = DEFAULT_NUM_KEYS;
                    maxInnerKeys = DEFAULT_NUM_KEYS;
                } else {
                    maxLeafKeys = maxInnerKeys;
                }
            } else if (maxInnerKeys == NOT_SPECIFIED) {
                maxInnerKeys = maxLeafKeys;
            }
            return new BPlusTree<K, V>(maxLeafKeys, maxInnerKeys, comparator);
        }

        public <K extends Comparable<K>, V> BPlusTree<K, V> naturalOrder() {
            return comparator(Comparator.naturalOrder());
        }
    }

    public void insert(K key, V value) {
        Split<K, V> result = root.insert(key, value);
        if (result != null) {
            // The old root was split into two parts.
            // We have to create a new root pointing to them
            InnerNode<K, V> rt = new InnerNode<>(options);
            rt.numKeys = 1;
            rt.keys[0] = result.key;
            rt.children[0] = result.left;
            rt.children[1] = result.right;
            root = rt;
        }
    }

    /**
     * Looks for the given key. If it is not found, it returns null. If it is found,
     * it returns the associated value.
     */
    public V find(K key) {
        Node<K, V> node = root;
        while (node instanceof InnerNode) { // need to traverse down to the leaf
            InnerNode<K, V> inner = (InnerNode<K, V>) node;
            int idx = inner.getLocation(key);
            node = inner.children[idx];
        }

        // We are @ leaf after while loop
        Leaf<K, V> leaf = (Leaf<K, V>) node;
        int idx = leaf.getLocation(key);
        if (idx < leaf.numKeys && leaf.keys[idx].equals(key)) {
            return leaf.values[idx];
        } else {
            return null;
        }
    }

    public void dump() {
        root.dump();
    }
}