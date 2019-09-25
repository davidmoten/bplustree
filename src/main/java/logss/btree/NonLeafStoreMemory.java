package logss.btree;

public final class NonLeafStoreMemory<K, V> implements NonLeafStore<K, V> {

    private final Node<K, V>[] children;
    private final K[] keys;
    private int numKeys; // number of keys

    @SuppressWarnings("unchecked")
    NonLeafStoreMemory(int maxKeys) {
        this.children = (Node<K, V>[]) new Node[maxKeys + 1];
        this.keys = (K[]) new Object[maxKeys];
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

    @Override
    public K key(int index) {
        return keys[index];
    }

    @Override
    public void setKey(int index, K key) {
        keys[index] = key;
    }

    @Override
    public void move(int mid, NonLeaf<K, V> other, int length) {
        other.store.setNumKeys(length);
        System.arraycopy(this.keys, mid, ((NonLeafStoreMemory<K, V>) other).keys, 0, length);
        System.arraycopy(this.children, mid, ((NonLeafStoreMemory<K, V>) other).children, 0, length + 1);
        numKeys = mid - 1;// this is important, so the middle one elevate to next
        // depth(height), inner node's key don't repeat itself
    }

}
