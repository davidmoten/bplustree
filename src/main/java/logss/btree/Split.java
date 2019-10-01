package logss.btree;

public final class Split<K, V> {
    final K key;
    final Node<K, V> left;
    final Node<K, V> right;

    public Split(K key, Node<K, V> left, Node<K, V> right) {
        this.key = key;
        this.left = left;
        this.right = right;
    }
}