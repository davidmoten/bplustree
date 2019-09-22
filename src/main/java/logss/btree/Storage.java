package logss.btree;

public class Storage<K, V> {

    InnerNodeStorage<K, V> createInner() {
        return new InnerNodeStorageMemory<K, V>(4);
    }
}
