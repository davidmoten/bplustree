package logss.btree;

public class NonLeafStorageMemory<K, V> implements NonLeafStorage<K, V> {

    private final Node<K, V>[] children;
    private int numKeys;

    @SuppressWarnings("unchecked")
    NonLeafStorageMemory(int maxKeys) {
        this.children = (Node<K, V>[]) new Node[maxKeys];
    }

    @Override
    public void setNumKeys(int numKeys) {
        this.numKeys = numKeys;
    }

    @Override
    public int numKeys() {
        return numKeys;
    }

    @Override
    public void setChild(int index, Node<K, V> node) {
        children[index] = node;
    }

}
