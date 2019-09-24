package logss.btree;

public final class NonLeafStoreMemory<K, V> implements NonLeafStore<K, V> {

    private final Node<K, V>[] children;
    private int numKeys;

    @SuppressWarnings("unchecked")
    NonLeafStoreMemory(int maxKeys) {
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

    @Override
    public Node<K, V> getChild(int index) {
        return children[index];
    }

}
