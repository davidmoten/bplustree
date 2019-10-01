package logss.btree.internal.memory;

import logss.btree.Factory;
import logss.btree.Leaf;
import logss.btree.NonLeaf;
import logss.btree.Options;

public final class FactoryMemory<K, V> implements Factory<K, V> {

    private final Options<K, V> options;

    public FactoryMemory(Options<K, V> options) {
        this.options = options;
    }

    public Leaf<K, V> createLeaf() {
        return new LeafMemory<K, V>(options, this);
    }

    public NonLeaf<K, V> createNonLeaf() {
        return new NonLeafMemory<K, V>(options, this);
    }

    @Override
    public void close() throws Exception {
        // do nothing
    }
}
