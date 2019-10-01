package logss.btree;

public interface Factory<K, V> extends AutoCloseable {

    Leaf<K, V> createLeaf();

    NonLeaf<K, V> createNonLeaf();

}