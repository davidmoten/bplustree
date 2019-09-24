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
    public void move(K[] keys, int mid, Leaf<K, V> sibling, int sNum) {
        System.arraycopy(this.keys, mid, ((LeafStoreMemory<K, V>) sibling.store).keys, 0, sNum);
        System.arraycopy(this.values, mid, ((LeafStoreMemory<K, V>) sibling.store).values, 0, sNum);
        this.numKeys = mid;
    }

    @Override
    public void setNumKeys(int numKeys) {
        this.numKeys = numKeys; 
    }

    @Override
    public void setValue(int idx, V value) {
        values[idx] =  value;
    }

}
