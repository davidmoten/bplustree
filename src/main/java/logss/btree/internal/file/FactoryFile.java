package logss.btree.internal.file;

import java.io.File;
import java.nio.ByteBuffer;

import logss.btree.Factory;
import logss.btree.Leaf;
import logss.btree.Node;
import logss.btree.NonLeaf;
import logss.btree.Options;
import logss.btree.Serializer;

public final class FactoryFile<K, V> implements Factory<K, V> {

    private final Options<K, V> options;
    // private final File directory;
    // private File indexFile;
    // private File dataFile;
    private byte[] data = new byte[1024 * 1024];
    private int index = 0;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;

    private final ByteBuffer bb = ByteBuffer.wrap(data);

    public FactoryFile(Options<K, V> options, File directory, Serializer<K> keySerializer,
            Serializer<V> valueSerializer) {
        this.options = options;
        // this.directory = directory;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    private static final int NODE_TYPE_BYTES = 1;
    private static final int NUM_KEYS_BYTES = 4;
    private static final int NUM_NODES_BYTES = 4;
    private static final int POSITION_BYTES = 4;

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
                + POSITION_BYTES // next leaf position
        ;
    }

    private long leafNextPosition() {
        int i = index;
        // shift by max size of a leaf node: numKeys, keys, values, next leaf position
        // (b+tree pointer to next leaf node)
        index += leafBytes();
        return i;
    }

    private int relativeLeafKeyPosition(int i) {
        return NODE_TYPE_BYTES + NUM_KEYS_BYTES + i * (keySerializer.maxSize() + valueSerializer.maxSize());
    }

    public K leafKey(long position, int i) {
        int p = (int) (position + relativeLeafKeyPosition(i));
        bb.position((int) p);
        return keySerializer.read(bb);
    }

    public int leafNumKeys(long position) {
        return bb.getInt((int) position);
    }

    public V leafValue(long position, int i) {
        int p = (int) (position + relativeLeafKeyPosition(i) + keySerializer.maxSize());
        bb.position(p);
        return valueSerializer.read(bb);
    }

    public void leafSetNumKeys(long position, int numKeys) {
        bb.putInt((int) position + NODE_TYPE_BYTES, numKeys);
    }

    public void leafSetValue(long position, int i, V value) {
        int p = (int) (position + relativeLeafKeyPosition(i) + keySerializer.maxSize() + keySerializer.maxSize());
        bb.position(p);
        valueSerializer.write(bb, value);
    }

    public void leafInsert(long position, int i, K key, V value) {
        int p = (int) (position + relativeLeafKeyPosition(i));
        bb.position(p);
        keySerializer.write(bb, key);
        bb.position(p + keySerializer.maxSize());
        valueSerializer.write(bb, value);
        // increment number of keys in leaf node
        bb.position((int) position);
        bb.putInt(leafNumKeys(position) + 1);
    }

    public void leafMove(long position, int start, int length, LeafFile<K, V> other) {
        int p = (int) (position + relativeLeafKeyPosition(start));
        byte[] bytes = new byte[length * (keySerializer.maxSize() + valueSerializer.maxSize())];
        bb.position(p);
        bb.get(bytes);
        p = (int) (other.position() + relativeLeafKeyPosition(0));
        bb.position(p);
        bb.put(bytes);
        // set the number of keys in source node to be `start`
        bb.position((int) position);
        bb.putInt(start);

        bb.position((int) other.position());
        bb.putInt(length);
    }

    public void leafSetNext(long position, LeafFile<K, V> sibling) {
        int p = (int) (position + relativeLeafKeyPosition(options.maxLeafKeys()));
        bb.putInt(p, (int) sibling.position());
    }

    public Leaf<K, V> leafNext(long position) {
        return new LeafFile<K, V>(options, this, position);
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
        return NODE_TYPE_BYTES + NUM_NODES_BYTES + options.maxNonLeafKeys() * (POSITION_BYTES + keySerializer.maxSize())
                + POSITION_BYTES;
    }

    private long nextNonLeafPosition() {
        int i = index;
        index += nonLeafBytes();
        return i;
    }

    public void nonLeafSetNumKeys(long position, int numKeys) {
        bb.putInt((int) position + NODE_TYPE_BYTES, numKeys);
    }

    public int nonLeafNumKeys(long position) {
        return bb.getInt((int) position);
    }

    public void nonLeafSetChild(long position, int i, NodeFile node) {
        int p = (int) (position + relativePositionNonLeafEntry(i));
        bb.putInt(p, (int) node.position());
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
        int size = relativePositionNonLeafEntry(mid + length) - relativeStart;
        bb.position((int) (position + relativeStart));
        byte[] bytes = new byte[size];
        bb.get(bytes);
        bb.position((int) (other.position() + relativePositionNonLeafEntry(0)));
        bb.put(bytes);
        nonLeafSetNumKeys(position, mid);
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
        // TODO
    }

}
