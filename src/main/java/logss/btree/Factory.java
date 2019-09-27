package logss.btree;

public final class Factory<K, V> {

    public Factory() {
    }

    public Leaf<K, V> createLeaf(Options<K,V> options) {
        return new LeafMemory<K,V>(options);
    }

    public NonLeaf<K, V> createNonLeaf(Options<K,V> options) {
        return new NonLeafMemory<K,V>(options);
    }
}
