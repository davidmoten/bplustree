package logss.btree;

public class Storage<K, V> {

    NonLeafStorage<K, V> createInner() {
        return new NonLeafStorageMemory<K, V>(4);
    }
}
