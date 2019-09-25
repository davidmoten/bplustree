package logss.btree;

public final class LeafStoreMemory<K, V> implements LeafStore<K, V> {

    private final K[] keys;
    private final V[] values;
    private int numKeys;

    @SuppressWarnings("unchecked")
    public LeafStoreMemory(int maxKeys) {
        keys = (K[]) new Object[maxKeys];
        values = (V[]) new Object[maxKeys];
    }

    @Override
    public V value(int index) {
        return values[index];
    }

    @Override
    public K key(int index) {
        return keys[index];
    }

    @Override
    public int numKeys() {
        return numKeys;
    }

    @Override
    public void move(int start, Leaf<K, V> sibling, int length) {
        System.arraycopy(this.keys, start, ((LeafStoreMemory<K, V>) sibling.store).keys, 0, length);
        System.arraycopy(this.values, start, ((LeafStoreMemory<K, V>) sibling.store).values, 0, length);
        this.numKeys = start;
    }

    @Override
    public void setNumKeys(int numKeys) {
        this.numKeys = numKeys;
    }

    @Override
    public void setValue(int idx, V value) {
        values[idx] = value;
    }

    @Override
    public void insert(int idx, K key, V value) {
        System.arraycopy(keys, idx, keys, idx + 1, numKeys - idx);
        System.arraycopy(values, idx, values, idx + 1, numKeys - idx);
        keys[idx] = key;
        values[idx] = value;
        numKeys++;
    }

}
