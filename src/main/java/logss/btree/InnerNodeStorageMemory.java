package logss.btree;

public class InnerNodeStorageMemory<K, V> implements InnerNodeStorage<K, V> {

    private final Node<K, V>[] children;
    private int numKeys;

    @SuppressWarnings("unchecked")
    InnerNodeStorageMemory(int maxKeys) {
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
