package logss.btree;

public interface Leaf<K, V> extends Node<K, V> {

    Options<K, V> options();

    V value(int index);

    void setNumKeys(int numKeys);

    void setValue(int idx, V value);

    void insert(int idx, K key, V value);

    void move(int start, Leaf<K, V> other, int length);

    void setNext(Leaf<K, V> sibling);

    Leaf<K, V> next();

    @Override
    default Split<K, V> insert(K key, V value) {
        // Simple linear search
        int i = getLocation(key);
        if (numKeys() == options().maxLeafKeys) { // The node was full. We must split it
            int mid = (options().maxLeafKeys + 1) / 2;
            int len = numKeys() - mid;
            Leaf<K, V> sibling = options().factory.createLeaf(options());
            move(mid, sibling, len);
            // System.arraycopy(this.keys, mid, sibling.keys, 0, sNum);
            // System.arraycopy(this.values, mid, sibling.values, 0, sNum);
            setNumKeys(mid);
            if (i < mid) {
                // Inserted element goes to left sibling
                insertNonfull(key, value, i);
            } else {
                // Inserted element goes to right sibling
                sibling.insertNonfull(key, value, i - mid);
            }
            setNext(sibling);
            // Notify the parent about the split
            return new Split<>(sibling.key(0), // make the right's key >=
                                               // result.key
                    this, sibling);
        } else {
            // The node was not full
            insertNonfull(key, value, i);
            return null;
        }
    }

    default void insertNonfull(K key, V value, int idx) {
        if (idx < numKeys() && options().uniqueKeys && key(idx).equals(key)) {
            // We are inserting a duplicate value, simply overwrite the old one
            setValue(idx, value);
        } else {
            // TODO put at end of duplicate keys
            // The key we are inserting is unique
            insert(idx, key, value);
        }
    }

}