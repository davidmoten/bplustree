package logss.btree;

public interface Factory<K, V> {

    Leaf<K, V> createLeaf();

    NonLeaf<K, V> createNonLeaf();

}