package logss.btree;

final class Split<K, V> {
    final K key;
    final Node<K, V> left;
    final Node<K, V> right;

    Split(K key, Node<K, V> left, Node<K, V> right) {
        this.key = key;
        this.left = left;
        this.right = right;
    }
}