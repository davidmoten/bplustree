package logss.btree;

public final class Storage<K, V> {

    private final int maxNonLeafKeys;
    private final int maxLeafKeys;

    public Storage(int maxNonLeafKeys, int maxLeafKeys) {
        this.maxNonLeafKeys = maxNonLeafKeys;
        this.maxLeafKeys = maxLeafKeys;
    }

    NonLeafStore<K, V> createNonLeafStore() {
        return new NonLeafStoreMemory<K, V>(maxNonLeafKeys);
    }

    LeafStore<K, V> createLeafStore() {
        return new LeafStoreMemory<K, V>(maxLeafKeys);
    }
}
