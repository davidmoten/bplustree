package logss.btree.internal.file;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

import logss.btree.Factory;
import logss.btree.Leaf;
import logss.btree.Node;
import logss.btree.NonLeaf;
import logss.btree.Options;
import logss.btree.Serializer;

public final class FactoryFile<K, V> implements Factory<K, V> {

    private static final int NEXT_NOT_PRESENT = -1;
    private final Options<K, V> options;
    // private final File directory;
    // private File indexFile;
    // private File dataFile;
    private int index = 0;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;

    private final FileChannel channel;
    private final ByteBuffer bb;

    public FactoryFile(Options<K, V> options, File directory, Serializer<K> keySerializer,
            Serializer<V> valueSerializer) {
        this.options = options;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        File file = new File(directory, "data.bin");
        file.delete();
        try {
            file.createNewFile();
            channel = (FileChannel) Files.newByteChannel(file.toPath(), EnumSet.of( //
                    StandardOpenOption.CREATE, //
                    StandardOpenOption.READ, //
                    StandardOpenOption.WRITE));
            bb = channel.map(FileChannel.MapMode.READ_WRITE, 0, 1024*1024);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
        bb.put(index, (byte) Leaf.TYPE);
        bb.putInt(index + leafBytes() - POSITION_BYTES, NEXT_NOT_PRESENT);
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
        int p = (int) (position + relativeLeafKeyPosition(i));
        bb.position((int) p);
        return keySerializer.read(bb);
    }

    public int leafNumKeys(long position) {
        return bb.getInt((int) position + NODE_TYPE_BYTES);
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
        int p = (int) (position + relativeLeafKeyPosition(i) + keySerializer.maxSize()
                + keySerializer.maxSize());
        bb.position(p);
        valueSerializer.write(bb, value);
    }

    public void leafInsert(long position, int i, K key, V value) {
        int relativeStart = relativeLeafKeyPosition(i);
        int relativeFinish = relativeLeafKeyPosition(leafNumKeys(position));

        bb.position((int) (position + relativeStart));
        byte[] bytes = new byte[relativeFinish - relativeStart];
        bb.get(bytes);

        // copy bytes across one key
        bb.position((int) (position + relativeLeafKeyPosition(i + 1)));
        bb.put(bytes);

        // write inserted key and value
        int p = (int) (position + relativeStart);
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
        bb.putInt(p, (int) sibling.position());
    }

    public Leaf<K, V> leafNext(long position) {
        int p = bb.getInt((int) position + relativeLeafKeyPosition(options.maxLeafKeys()));
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
        int i = index;
        bb.put(index, (byte) NonLeaf.TYPE);
        index += nonLeafBytes();
        return i;
    }

    public void nonLeafSetNumKeys(long position, int numKeys) {
        bb.putInt((int) position + NODE_TYPE_BYTES, numKeys);
    }

    public int nonLeafNumKeys(long position) {
        return bb.getInt((int) position + NODE_TYPE_BYTES);
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
        int size = relativePositionNonLeafEntry(mid + length + 1) - relativeStart;
        bb.position((int) (position + relativeStart));
        byte[] bytes = new byte[size];
        bb.get(bytes);
        bb.position((int) (other.position() + relativePositionNonLeafEntry(0)));
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
        channel.close();
    }

    // visible for testing
    public byte[] data() {
        return bb.array();
    }

}
