package com.github.davidmoten.bplustree;

import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.bplustree.internal.Node;
import com.github.davidmoten.bplustree.internal.NonLeaf;

public final class NodeWrapper<K, V> {

    private final Node<K, V> node;

    public NodeWrapper(Node<K, V> node) {
        this.node = node;
    }

    public static <K, V> NodeWrapper<K, V> root(BPlusTree<K, V> tree) {
        return new NodeWrapper<K, V>(tree.root());
    }

    public List<K> keys() {
        return node.keys();
    }

    public List<NodeWrapper<K, V>> children() {
        NonLeaf<K, V> nd = (NonLeaf<K, V>) node;
        List<NodeWrapper<K, V>> list = new ArrayList<>();
        for (int i = 0; i < nd.numKeys() + 1; i++) {
            if (nd.child(i) != null) {
                list.add(new NodeWrapper<K, V>(nd.child(i)));
            }
        }
        return list;
    }

}
