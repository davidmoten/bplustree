package logss;

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
    /**
     * Pointer to the root node. It may be a leaf or an inner node, but it is never
     * null.
     */
    private Node<K, V> root;
    /** the maximum number of keys in the leaf node, M must be > 0 */

    private final Options<K> options;

    /** Create a new empty tree. */
    public BPlusTree(int n, Comparator<K> comparator) {
        this(n, n, comparator);
    }

    public BPlusTree(int maxLeafKeys, int maxInnerKeys, Comparator<K> comparator) {
        this.options = new Options<K>(maxLeafKeys, maxInnerKeys, comparator);
        this.root = new Leaf<K, V>(options);
    }

    public void insert(K key, V value) {
        Split<K, V> result = root.insert(key, value);
        if (result != null) {
            // The old root was split into two parts.
            // We have to create a new root pointing to them
            InnerNode<K, V> rt = new InnerNode<>(options);
            rt.num = 1;
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
        while (node instanceof BPlusTree.InnerNode) { // need to traverse down to the leaf
            InnerNode<K, V> inner = (InnerNode<K, V>) node;
            int idx = inner.getLocation(key);
            node = inner.children[idx];
        }

        // We are @ leaf after while loop
        Leaf<K, V> leaf = (Leaf<K, V>) node;
        int idx = leaf.getLocation(key);
        if (idx < leaf.num && leaf.keys[idx].equals(key)) {
            return leaf.values[idx];
        } else {
            return null;
        }
    }

    public void dump() {
        root.dump();
    }

    static final class Options<K> {
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

    static abstract class Node<K, V> {
        int num; // number of keys
        K[] keys;

        abstract public int getLocation(K key);

        // returns null if no split, otherwise returns split info
        abstract public Split<K, V> insert(K key, V value);

        abstract public void dump();

    }

    @SuppressWarnings("unchecked")
    static class Leaf<K, V> extends Node<K, V> {
        private final Options<K> options;
        private final V[] values;

        public Leaf(Options<K> options) {
            this.options = options;
            this.values = (V[]) new Object[options.maxLeafKeys];
            this.keys = (K[]) new Object[options.maxLeafKeys];
        }

        /**
         * Returns the position where 'key' should be inserted in a leaf node that has
         * the given keys.
         */
        public int getLocation(K key) {
            // Simple linear search. Faster for small values of N or M, binary search would
            // be faster for larger M / N
            for (int i = 0; i < num; i++) {
                if (options.comparator.compare(keys[i], key) >= 0) {
                    return i;
                }
            }
            return num;
        }

        public Split<K, V> insert(K key, V value) {
            // Simple linear search
            int i = getLocation(key);
            if (this.num == options.maxLeafKeys) { // The node was full. We must split it
                int mid = (options.maxLeafKeys + 1) / 2;
                int sNum = this.num - mid;
                Leaf<K, V> sibling = new Leaf<K, V>(options);
                sibling.num = sNum;
                System.arraycopy(this.keys, mid, sibling.keys, 0, sNum);
                System.arraycopy(this.values, mid, sibling.values, 0, sNum);
                this.num = mid;
                if (i < mid) {
                    // Inserted element goes to left sibling
                    this.insertNonfull(key, value, i);
                } else {
                    // Inserted element goes to right sibling
                    sibling.insertNonfull(key, value, i - mid);
                }
                // Notify the parent about the split
                Split<K, V> result = new Split<>(sibling.keys[0], // make the right's key >=
                                                                  // result.key
                        this, sibling);
                return result;
            } else {
                // The node was not full
                this.insertNonfull(key, value, i);
                return null;
            }
        }

        private void insertNonfull(K key, V value, int idx) {
            if (idx < num && keys[idx].equals(key)) {
                // We are inserting a duplicate value, simply overwrite the old one
                values[idx] = value;
            } else {
                // The key we are inserting is unique
                System.arraycopy(keys, idx, keys, idx + 1, num - idx);
                System.arraycopy(values, idx, values, idx + 1, num - idx);

                keys[idx] = key;
                values[idx] = value;
                num++;
            }
        }

        public void dump() {
            System.out.println("lNode h==0");
            for (int i = 0; i < num; i++) {
                System.out.println(keys[i]);
            }
        }
    }

    static class InnerNode<K, V> extends Node<K, V> {

        private final Options<K> options;
        private final Node<K, V>[] children;

        @SuppressWarnings("unchecked")
        InnerNode(Options<K> options) {
            this.options = options;
            this.children = new Node[options.maxInnerKeys + 1];
            this.keys = (K[]) new Object[options.maxInnerKeys];
        }

        /**
         * Returns the position where 'key' should be inserted in an inner node that has
         * the given keys.
         */
        public int getLocation(K key) {
            // Simple linear search. Faster for small values of N or M
            for (int i = 0; i < num; i++) {
                if (options.comparator.compare(keys[i], key) > 0) {
                    return i;
                }
            }
            return num;
            // Binary search is faster when N or M is big,
        }

        public Split<K, V> insert(K key, V value) {
            /*
             * Early split if node is full. This is not the canonical algorithm for B+
             * trees, but it is simpler and it does break the definition which might result
             * in immature split, which might not be desired in database because additional
             * split lead to tree's height increase by 1, thus the number of disk read so
             * first search to the leaf, and split from bottom up is the correct approach.
             */

            if (this.num == options.maxInnerKeys) { // Split
                int mid = (options.maxInnerKeys + 1) / 2;
                int sNum = this.num - mid;
                InnerNode<K, V> sibling = new InnerNode<K, V>(options);
                sibling.num = sNum;
                System.arraycopy(this.keys, mid, sibling.keys, 0, sNum);
                System.arraycopy(this.children, mid, sibling.children, 0, sNum + 1);

                this.num = mid - 1;// this is important, so the middle one elevate to next
                                   // depth(height), inner node's key don't repeat itself

                // Set up the return variable
                Split<K, V> result = new Split<>(this.keys[mid - 1], this, sibling);

                // Now insert in the appropriate sibling
                if (options.comparator.compare(key, result.key) < 0) {
                    this.insertNonfull(key, value);
                } else {
                    sibling.insertNonfull(key, value);
                }
                return result;

            } else {// No split
                this.insertNonfull(key, value);
                return null;
            }
        }

        private void insertNonfull(K key, V value) {
            // Simple linear search
            int idx = getLocation(key);
            Split<K,V> result = children[idx].insert(key, value);

            if (result != null) {
                if (idx == num) {
                    // Insertion at the rightmost key
                    keys[idx] = result.key;
                    children[idx] = result.left;
                    children[idx + 1] = result.right;
                    num++;
                } else {
                    // Insertion not at the rightmost key
                    // shift i>idx to the right
                    System.arraycopy(keys, idx, keys, idx + 1, num - idx);
                    System.arraycopy(children, idx, children, idx + 1, num - idx + 1);

                    children[idx] = result.left;
                    children[idx + 1] = result.right;
                    keys[idx] = result.key;
                    num++;
                }
            } // else the current node is not affected
        }

        /**
         * This one only dump integer key
         */
        public void dump() {
            System.out.println("iNode h==?");
            for (int i = 0; i < num; i++) {
                children[i].dump();
                System.out.print('>');
                System.out.println(keys[i]);
            }
            children[num].dump();
        }
    }

    final static class Split<K, V> {
        final K key;
        final Node<K, V> left;
        final Node<K, V> right;

        public Split(K key, Node<K, V> left, Node<K, V> right) {
            this.key = key;
            this.left = left;
            this.right = right;
        }
    }
}