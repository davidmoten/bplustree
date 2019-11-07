package com.github.davidmoten.bplustree;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.davidmoten.bplustree.internal.memory.FactoryMemory;

public class BPlusTree<K, V> implements AutoCloseable {

    private final Options<K, V> options;
    private final Factory<K, V> factory;

    /**
     * Pointer to the root node. It may be a leaf or an inner node, but it is never
     * null.
     */
    private Node<K, V> root;

    /** Create a new empty tree. */
    private BPlusTree(int maxLeafKeys, int maxInnerKeys, boolean uniqueKeys, Comparator<? super K> comparator,
            FactoryProvider<K, V> factoryProvider) {
        this.options = new Options<K, V>(maxLeafKeys, maxInnerKeys, uniqueKeys, comparator, factoryProvider);
        this.factory = options.factoryProvider().createFactory(options);
        this.root = factory.loadOrCreateRoot();
        factory.root(root);
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }

    public static final class Builder<K, V> {

        private static final int NOT_SPECIFIED = -1;

        private static final int DEFAULT_NUM_KEYS = 4;

        private int maxLeafKeys = NOT_SPECIFIED;
        private int maxInnerKeys = NOT_SPECIFIED;

        private boolean uniqueKeys = false;
        private FactoryProvider<K, V> factoryProvider = options -> new FactoryMemory<K, V>(options);

        Builder() {
            // prevent instantiation
        }

        public Builder<K, V> factoryProvider(FactoryProvider<K, V> factoryProvider) {
            this.factoryProvider = factoryProvider;
            return this;
        }

        public Builder<K, V> maxLeafKeys(int maxLeafKeys) {
            this.maxLeafKeys = maxLeafKeys;
            return this;
        }

        public Builder<K, V> maxNonLeafKeys(int maxInnerKeys) {
            this.maxInnerKeys = maxInnerKeys;
            return this;
        }

        public Builder<K, V> maxKeys(int maxKeys) {
            maxLeafKeys(maxKeys);
            return maxNonLeafKeys(maxKeys);
        }

        public Builder<K, V> uniqueKeys(boolean uniqueKeys) {
            this.uniqueKeys = uniqueKeys;
            return this;
        }

        public BPlusTree<K, V> comparator(Comparator<? super K> comparator) {
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

            return new BPlusTree<K, V>(maxLeafKeys, maxInnerKeys, uniqueKeys, comparator, factoryProvider);
        }

        @SuppressWarnings("unchecked")
        public BPlusTree<K, V> naturalOrder() {
            return comparator((Comparator<K>) (Comparator<?>) Comparator.naturalOrder());
        }

        public Builder<K, V> uniqueKeys() {
            return uniqueKeys(true);
        }
    }

    public void insert(K key, V value) {
        Split<K, V> result = root.insert(key, value);
        if (result != null) {
            // The root is split into two parts.
            // We create a new root pointing to them
            NonLeaf<K, V> node = //
                    factory //
                            .createNonLeaf();
            node.setNumKeys(1);
            node.setKey(0, result.key);
            node.setChild(0, result.left);
            node.setChild(1, result.right);
            root = node;
            factory.root(root);
        }
    }

    /**
     * Looks for the given key. If it is not found, it returns null. If it is found,
     * it returns the associated value.
     */
    public V findFirst(K key) {
        Leaf<K, V> leaf = findFirstLeaf(key);
        int idx = leaf.getLocation(key);
        if (idx < leaf.numKeys() && leaf.key(idx).equals(key)) {
            return leaf.value(idx);
        } else {
            return null;
        }
    }

    public Iterable<V> find(K key) {

        return new Iterable<V>() {

            @Override
            public Iterator<V> iterator() {
                throw new UnsupportedOperationException("not implemented yet");
            }
        };
    }

    private Leaf<K, V> findFirstLeaf(K key) {
        Node<K, V> node = root;
        while (node instanceof NonLeaf) { // need to traverse down to the leaf
            NonLeaf<K, V> inner = (NonLeaf<K, V>) node;
            int idx = inner.getLocation(key);
            node = inner.child(idx);
        }
        // We are @ leaf after while loop
        return (Leaf<K, V>) node;
    }

    /**
     * Returns an in-order sequence of values whose keys are >= start and < finish.
     * 
     * @param startInclusive
     *            inclusive end of search
     * @param finishExclusive
     *            exclusive end of search
     * @return in-order sequence of values whose keys are >= start and < finish
     */
    public Iterable<V> find(K startInclusive, K finishExclusive) {
        return find(startInclusive, finishExclusive, false);
    }

    public Iterable<V> find(K startInclusive, K finish, boolean isFinishInclusive) {
        return new Iterable<V>() {

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    Leaf<K, V> leaf = findFirstLeaf(startInclusive);
                    int idx = leaf.getLocation(startInclusive);
                    V value;

                    @Override
                    public boolean hasNext() {
                        load();
                        return value != null;
                    }

                    @Override
                    public V next() {
                        load();
                        V v = value;
                        value = null;
                        if (v == null) {
                            throw new NoSuchElementException();
                        } else {
                            return v;
                        }
                    }

                    private void load() {
                        if (value != null) {
                            return;
                        }
                        while (true) {
                            if (leaf == null) {
                                return;
                            } else if (idx < leaf.numKeys()) {
                                int c = options.comparator().compare(leaf.key(idx), finish);
                                if (c < 0 || (c == 0 && isFinishInclusive)) {
                                    value = leaf.value(idx);
                                    idx++;
                                } else {
                                    // don't search further
                                    leaf = null;
                                }
                                return;
                            } else {
                                leaf = leaf.next();
                                idx = 0;
                            }
                        }
                    }

                };
            }

        };
    }

    /**
     * For the situation when uniqueness is false, when entries are inserted with
     * the same key they are inserted before the last entry. As a consequence if we
     * want to preserve the insert order in the returned values from a find then we
     * need to collect entries with the same key and then emit them in reverse
     * order. If there are a lot of keys with the same value then an
     * {@link OutOfMemoryError} might be thrown.
     * 
     * @param startInclusive
     * @param finishExclusive
     * @return values of entries in searched for key range preserving insert order
     */
    public Iterable<V> findPreserveDuplicateInsertOrder(K startInclusive, K finishExclusive) {
        return findPreserveInsertOrder(startInclusive, finishExclusive, false);
    }

    /**
     * For the situation when uniqueness is false, when entries are inserted with
     * the same key they are inserted before the last entry. As a consequence if we
     * want to preserve the insert order in the returned values from a find then we
     * need to collect entries with the same key and then emit them in reverse
     * order. If there are a lot of keys with the same value then an
     * {@link OutOfMemoryError} might be thrown.
     * 
     * @param startInclusive
     * @param finish
     * @param isFinishInclusive
     * @return values of entries in searched for key range preserving insert order
     */
    public Iterable<V> findPreserveInsertOrder(K startInclusive, K finish, boolean isFinishInclusive) {
        return new Iterable<V>() {

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    Leaf<K, V> leaf = findFirstLeaf(startInclusive);
                    int idx = leaf.getLocation(startInclusive);
                    K currentKey;
                    List<V> values = new ArrayList<V>();
                    int valuesIdx = 0;
                    List<V> nextValues = new ArrayList<V>();

                    @Override
                    public boolean hasNext() {
                        load();
                        return valuesIdx < values.size();
                    }

                    @Override
                    public V next() {
                        load();
                        System.out.println("valuesIdx="+ valuesIdx + ", values=" + values);
                        if (valuesIdx >= values.size()) {
                            throw new NoSuchElementException();
                        } else {
                            // emit in reverse order
                            // clear the value from the list to enable early GC
                            V v =  values.set(values.size() - valuesIdx -1, null);
                            valuesIdx++;
                            return v;
                        }
                    }

                    private void load() {
                        if (valuesIdx < values.size()) {
                            return;
                        }
                        valuesIdx = 0;
                        values.clear();
                        // swap values and nextValues
                        List<V> temp = values;
                        values = nextValues;
                        nextValues = temp;
                        while (true) {
                            if (leaf == null) {
                                return;
                            } else if (idx < leaf.numKeys()) {
                                K key = leaf.key(idx);
                                int c = options.comparator().compare(key, finish);
                                if (c < 0 || (c == 0 && isFinishInclusive)) {
                                    if (currentKey == null) {
                                        currentKey = key;
                                    }
                                    if (options.comparator().compare(currentKey, key) == 0) {
                                        values.add(leaf.value(idx));
                                        idx++;
                                    } else {
                                        //key has changed
                                        currentKey = key;
                                        nextValues.add(leaf.value(idx));
                                        idx++;
                                        // key has changed so we have found the next key sequence
                                        return;
                                    }
                                } else {
                                    // don't search further
                                    leaf = null;
                                    return;
                                }
                            } else {
                                leaf = leaf.next();
                                idx = 0;
                            }
                        }
                    }
                };
            }

        };
    }

    public void print() {
        print(System.out);
    }

    public void print(PrintStream out) {
        print(root, 0, out);
    }

    private static <K, V> void print(Node<K, V> node, int level, PrintStream out) {
        if (node instanceof Leaf) {
            print((Leaf<K, V>) node, level, out);
        } else {
            print((NonLeaf<K, V>) node, level, out);
        }
    }

    private static <K, V> void print(Leaf<K, V> node, int level, PrintStream out) {
        out.print(indent(level));
        out.print("Leaf: ");
        int n = node.numKeys();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                out.print(", ");
            }
            out.print(node.key(i));
            out.print("->");
            out.print(node.value(i));
        }
        if (node.next() != null) {
            out.print("| -> " + node.next().keys());
        }
        out.println();
    }

    private static <K, V> void print(NonLeaf<K, V> node, int level, PrintStream out) {
        out.print(indent(level));
        out.println("NonLeaf");
        int n = node.numKeys();
        for (int i = 0; i < n; i++) {
            Node<K, V> nd = node.child(i);
            print(nd, level + 1, out);
            out.print(indent(level) + node.key(i));
            out.println();
        }
        if (node.child(n) != null) {
            print(node.child(n), level + 1, out);
        }
        out.println();
    }

    private static String indent(int level) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < level; i++) {
            b.append("  ");
        }
        return b.toString();
    }

    public Node<K, V> root() {
        return root;
    }

    @Override
    public void close() throws Exception {
        factory.close();
    }

    public void commit() {
        factory.commit();
    }

}