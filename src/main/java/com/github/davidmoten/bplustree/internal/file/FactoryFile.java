package com.github.davidmoten.bplustree.internal.file;

import java.io.File;

import com.github.davidmoten.bplustree.Factory;
import com.github.davidmoten.bplustree.FactoryProvider;
import com.github.davidmoten.bplustree.Leaf;
import com.github.davidmoten.bplustree.Node;
import com.github.davidmoten.bplustree.NonLeaf;
import com.github.davidmoten.bplustree.Options;
import com.github.davidmoten.bplustree.Serializer;
import com.github.davidmoten.bplustree.internal.LargeMappedByteBuffer;

public final class FactoryFile<K, V> implements Factory<K, V> {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        File directory;
        int initialFileSize = 1024 * 1024;
        int thresholdBytesToSwitchToLinearIncreaseInFileSize = 100 * 1024 * 1024;
        int linearIncreaseBytes = 100 * 1024 * 1024;

        Builder() {
            // reduce visibility
        }

        public Builder directory(String directory) {
            return directory(new File(directory));
        }

        public Builder directory(File directory) {
            this.directory = directory;
            return this;
        }

        public Builder initialFileSizeBytes(int size) {
            this.initialFileSize = size;
            return this;
        }

        public Builder thresholdBytesToSwitchToLinearIncreaseInFileSize(int thresholdBytes) {
            this.thresholdBytesToSwitchToLinearIncreaseInFileSize = thresholdBytes;
            return this;
        }

        public Builder linearIncreaseBytes(int sizeBytes) {
            this.linearIncreaseBytes = sizeBytes;
            return this;
        }

        public <K> Builder2<K> keySerializer(Serializer<K> serializer) {
            return new Builder2<K>(this, serializer);
        }
    }

    public static final class Builder2<K> {

        private final Builder b;
        private final Serializer<K> keySerializer;

        public Builder2(Builder builder, Serializer<K> keySerializer) {
            this.b = builder;
            this.keySerializer = keySerializer;
        }

        public <V> FactoryProvider<K, V> valueSerializer(Serializer<V> valueSerializer) {
            return options -> new FactoryFile<K, V>(options, b.directory, keySerializer,
                    valueSerializer, b.initialFileSize);
        }

    }

    private static final int NODE_TYPE_BYTES = 1;
    private static final int NUM_KEYS_BYTES = 4;
    private static final int NUM_NODES_BYTES = 4;
    private static final int POSITION_BYTES = 4;
    private static final int NEXT_NOT_PRESENT = -1;
    private final Options<K, V> options;
    private long index = 0;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;
    private final LargeMappedByteBuffer bb;

    public FactoryFile(Options<K, V> options, File directory, Serializer<K> keySerializer,
            Serializer<V> valueSerializer, int segmentSizeBytes) {
        this.options = options;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.bb = new LargeMappedByteBuffer(directory, segmentSizeBytes);
    }

    //////////////////////////////////////////////////
    // Format of a Leaf
    // NODE_TYPE NUM_KEYS (KEY VALUE)* NEXT_LEAF_POSITION
    // where
    // NODE_TYPE is one byte (0 = Leaf, 1 = NonLeaf)
    // NUM_KEYS is 4 bytes signed int
    // KEY is a byte array of fixed size
    // VALUE is a byte array of fixed size
    // NEXT_LEAF_POSITION is 4 bytes signed int
    // Every Leaf has space allocated for maxLeafKeys key value pairs
    //////////////////////////////////////////////////

    @Override
    public Leaf<K, V> createLeaf() {
        return new LeafFile<K, V>(options, this, leafNextPosition());
    }

    private int leafBytes() {
        return relativeLeafKeyPosition(options.maxLeafKeys()) //
                + POSITION_BYTES; // next leaf position
    }

    private long leafNextPosition() {
        long i = index;
        bb.position(index);
        bb.put((byte) Leaf.TYPE);
        bb.position(index + leafBytes() - POSITION_BYTES);
        bb.putInt(NEXT_NOT_PRESENT);
        // shift by max size of a leaf node: numKeys, keys, values, next leaf position
        // (b+tree pointer to next leaf node)
        index += leafBytes();
        return i;
    }

    private int relativeLeafKeyPosition(int i) {
        return NODE_TYPE_BYTES + NUM_KEYS_BYTES
                + i * (keySerializer.maxSize() + valueSerializer.maxSize());
    }

    public K leafKey(long position, int i) {
        long p = position + relativeLeafKeyPosition(i);
        bb.position(p);
        return keySerializer.read(bb);
    }

    public int leafNumKeys(long position) {
        bb.position(position + NODE_TYPE_BYTES);
        return bb.getInt();
    }

    public V leafValue(long position, int i) {
        long p = position + relativeLeafKeyPosition(i) + keySerializer.maxSize();
        bb.position(p);
        return valueSerializer.read(bb);
    }

    public void leafSetNumKeys(long position, int numKeys) {
        bb.position(position + NODE_TYPE_BYTES);
        bb.putInt(numKeys);
    }

    public void leafSetValue(long position, int i, V value) {
        long p = position + relativeLeafKeyPosition(i) + keySerializer.maxSize()
                + keySerializer.maxSize();
        bb.position(p);
        valueSerializer.write(bb, value);
    }

    public void leafInsert(long position, int i, K key, V value) {
        int relativeStart = relativeLeafKeyPosition(i);
        int relativeFinish = relativeLeafKeyPosition(leafNumKeys(position));

        bb.position(position + relativeStart);
        byte[] bytes = new byte[relativeFinish - relativeStart];
        bb.get(bytes);

        // copy bytes across one key
        bb.position(position + relativeLeafKeyPosition(i + 1));
        bb.put(bytes);

        // write inserted key and value
        long p = position + relativeStart;
        bb.position(p);
        keySerializer.write(bb, key);
        bb.position(p + keySerializer.maxSize());
        valueSerializer.write(bb, value);
        // increment number of keys in leaf node
        leafSetNumKeys(position, leafNumKeys(position) + 1);
    }

    public void leafMove(long position, int start, int length, LeafFile<K, V> other) {
        int relativeStart = relativeLeafKeyPosition(start);
        int relativeEnd = relativeLeafKeyPosition(start + length);
        byte[] bytes = new byte[relativeEnd - relativeStart];
        bb.position((int) (position + relativeStart));
        bb.get(bytes);
        int p = (int) (other.position() + relativeLeafKeyPosition(0));
        bb.position(p);
        bb.put(bytes);
        // set the number of keys in source node to be `start`
        leafSetNumKeys(position, start);
        leafSetNumKeys(other.position(), length);
    }

    public void leafSetNext(long position, LeafFile<K, V> sibling) {
        final int p;
        if (sibling == null) {
            p = NEXT_NOT_PRESENT;
        } else {
            p = (int) (position + relativeLeafKeyPosition(options.maxLeafKeys()));
        }
        bb.position(p);
        bb.putInt((int) sibling.position());
    }

    public Leaf<K, V> leafNext(long position) {
        bb.position((int) position + relativeLeafKeyPosition(options.maxLeafKeys()));
        int p = bb.getInt();
        if (p == NEXT_NOT_PRESENT) {
            return null;
        } else {
            return new LeafFile<K, V>(options, this, p);
        }
    }

    //////////////////////////////////////////////////
    // Format of a NonLeaf
    // NODE_TYPE NUM_KEYS (LEFT_CHILD_POSITION KEY)* RIGHT_CHILD_POSITION
    // where
    // NODE_TYPE is one byte (0 = Leaf, 1 = NonLeaf)
    // NUM_KEYS is 4 bytes signed int
    // LEFT_CHILD_POSITION is 4 bytes signed int
    // KEY is a fixed size byte array
    // RIGHT_CHILD_POSITION is 4 bytes signed int
    // Every NonLeaf has space allocated for maxNonLeafKeys keys
    //////////////////////////////////////////////////

    @Override
    public NonLeaf<K, V> createNonLeaf() {
        return new NonLeafFile<K, V>(options, this, nextNonLeafPosition());
    }

    private int nonLeafBytes() {
        // every key has a child node to the left and the final key has a child node to
        // the right as well as the left
        return NODE_TYPE_BYTES + NUM_NODES_BYTES
                + options.maxNonLeafKeys() * (POSITION_BYTES + keySerializer.maxSize())
                + POSITION_BYTES;
    }

    private long nextNonLeafPosition() {
        long i = index;
        bb.position(index);
        bb.put((byte) NonLeaf.TYPE);
        index += nonLeafBytes();
        return i;
    }

    public void nonLeafSetNumKeys(long position, int numKeys) {
        bb.position((int) position + NODE_TYPE_BYTES);
        bb.putInt(numKeys);
    }

    public int nonLeafNumKeys(long position) {
        bb.position((int) position + NODE_TYPE_BYTES);
        return bb.getInt();
    }

    public void nonLeafSetChild(long position, int i, NodeFile node) {
        int p = (int) (position + relativePositionNonLeafEntry(i));
        bb.position(p);
        bb.putInt((int) node.position());
    }

    private int relativePositionNonLeafEntry(int i) {
        return NODE_TYPE_BYTES + NUM_KEYS_BYTES + i * (POSITION_BYTES + keySerializer.maxSize());
    }

    public Node<K, V> nonLeafChild(long position, int i) {
        bb.position((int) (position + relativePositionNonLeafEntry(i)));
        int pos = bb.getInt();
        return readNode(pos);
    }

    private Node<K, V> readNode(int pos) {
        bb.position(pos);
        int type = bb.get();
        if (type == Leaf.TYPE) {
            return new LeafFile<>(options, this, pos);
        } else {
            return new NonLeafFile<>(options, this, pos);
        }
    }

    public K nonLeafKey(long position, int i) {
        bb.position((int) (position + relativePositionNonLeafEntry(i) + POSITION_BYTES));
        return keySerializer.read(bb);
    }

    public void nonLeafSetKey(long position, int i, K key) {
        bb.position((int) (position + relativePositionNonLeafEntry(i) + POSITION_BYTES));
        keySerializer.write(bb, key);
    }

    public void nonLeafMove(long position, int mid, int length, NonLeafFile<K, V> other) {
        // read array corresponding to latter half of source node and put at beginning
        // of other node
        int relativeStart = relativePositionNonLeafEntry(mid);
        int size = relativePositionNonLeafEntry(mid + length + 1) - relativeStart;
        bb.position((int) (position + relativeStart));
        byte[] bytes = new byte[size];
        bb.get(bytes);
        int newPosition = (int) (other.position() + relativePositionNonLeafEntry(0));
        bb.position(newPosition);
        bb.put(bytes);
        nonLeafSetNumKeys(position, mid - 1);
        nonLeafSetNumKeys(other.position(), length);
    }

    public void nonLeafInsert(long position, int i, K key, NodeFile left) {
        int numKeys = nonLeafNumKeys(position);
        int relativeStart = relativePositionNonLeafEntry(i);
        int relativeEnd = relativePositionNonLeafEntry(numKeys);
        bb.position((int) (position + relativeStart));
        byte[] bytes = new byte[relativeEnd - relativeStart];
        bb.get(bytes);
        bb.position((int) (position + relativePositionNonLeafEntry(i + 1)));
        bb.put(bytes);
        bb.position(relativeStart);
        bb.putInt((int) left.position());
        keySerializer.write(bb, key);
        nonLeafSetNumKeys(position, numKeys + 1);
    }

    @Override
    public void close() throws Exception {
        bb.close();
    }

}
