package logss.btree;

class InnerNode<K, V> implements Node<K, V> {

    private final Options<K> options;
    final Node<K, V>[] children;
    final K[] keys;
    int numKeys; // number of keys

    @SuppressWarnings("unchecked")
    InnerNode(Options<K> options) {
        this.options = options;
        this.children = new Node[options.maxInnerKeys + 1];
        this.keys = (K[]) new Object[options.maxInnerKeys];
    }

    /**
     * Returns the position where 'key' should be inserted in an inner node that has
     * the given keys.
     */
    @Override
    public int getLocation(K key) {
        // Simple linear search. Faster for small values of N or M
        for (int i = 0; i < numKeys; i++) {
            if (options.comparator.compare(keys[i], key) > 0) {
                return i;
            }
        }
        return numKeys;
        // Binary search is faster when N or M is big,
    }

    @Override
    public Split<K, V> insert(K key, V value) {
        /*
         * Early split if node is full. This is not the canonical algorithm for B+
         * trees, but it is simpler and it does break the definition which might result
         * in immature split, which might not be desired in database because additional
         * split lead to tree's height increase by 1, thus the number of disk read so
         * first search to the leaf, and split from bottom up is the correct approach.
         */

        if (this.numKeys == options.maxInnerKeys) { // Split
            int mid = (options.maxInnerKeys + 1) / 2;
            int sNum = this.numKeys - mid;
            InnerNode<K, V> sibling = new InnerNode<K, V>(options);
            sibling.numKeys = sNum;
            System.arraycopy(this.keys, mid, sibling.keys, 0, sNum);
            System.arraycopy(this.children, mid, sibling.children, 0, sNum + 1);

            this.numKeys = mid - 1;// this is important, so the middle one elevate to next
            // depth(height), inner node's key don't repeat itself

            // Set up the return variable
            Split<K, V> result = new Split<>(this.keys[mid - 1], this, sibling);

            // Now insert in the appropriate sibling
            if (options.comparator.compare(key, result.key) < 0) {
                this.insertNonfull(key, value);
            } else {
                sibling.insertNonfull(key, value);
            }
            return result;

        } else {// No split
            this.insertNonfull(key, value);
            return null;
        }
    }

    private void insertNonfull(K key, V value) {
        // Simple linear search
        int idx = getLocation(key);
        Split<K, V> result = children[idx].insert(key, value);

        if (result != null) {
            if (idx == numKeys) {
                // Insertion at the rightmost key
                keys[idx] = result.key;
                children[idx] = result.left;
                children[idx + 1] = result.right;
                numKeys++;
            } else {
                // Insertion not at the rightmost key
                // shift i>idx to the right
                System.arraycopy(keys, idx, keys, idx + 1, numKeys - idx);
                System.arraycopy(children, idx, children, idx + 1, numKeys - idx + 1);

                children[idx] = result.left;
                children[idx + 1] = result.right;
                keys[idx] = result.key;
                numKeys++;
            }
        } // else the current node is not affected
    }

    /**
     * This one only dump integer key
     */
    @Override
    public void dump() {
        System.out.println("iNode h==?");
        for (int i = 0; i < numKeys; i++) {
            children[i].dump();
            System.out.print('>');
            System.out.println(keys[i]);
        }
        children[numKeys].dump();
    }
}