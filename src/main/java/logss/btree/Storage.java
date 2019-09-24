package logss.btree;

public class Storage<K, V> {

    private final int maxNonLeafKeys;
    private final int maxLeafKeys;

    public Storage(int maxNonLeafKeys, int maxLeafKeys) {
        this.maxNonLeafKeys = maxNonLeafKeys;
        this.maxLeafKeys = maxLeafKeys;
    }

    NonLeafStorage<K, V> createNonLeafStore() {
        return new NonLeafStorageMemory<K, V>(maxNonLeafKeys);
    }

    LeafStorage<K, V> createLeafStore() {
        return new LeafStorageMemory<K, V>(maxLeafKeys);
    }
}
