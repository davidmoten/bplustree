package logss.btree.internal.file;

import java.io.File;
import java.nio.ByteBuffer;

import logss.btree.Factory;
import logss.btree.Leaf;
import logss.btree.NonLeaf;
import logss.btree.Options;
import logss.btree.Serializer;

public final class FactoryFile<K, V> implements Factory<K, V> {

    private final Options<K, V> options;
//    private final File directory;
//    private File indexFile;
//    private File dataFile;
    private byte[] data = new byte[1024 * 1024];
    private int index = 0;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;

    private final ByteBuffer bb = ByteBuffer.wrap(data);

    public FactoryFile(Options<K, V> options, File directory, Serializer<K> keySerializer,
            Serializer<V> valueSerializer) {
        this.options = options;
//        this.directory = directory;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    private static final int NUM_KEYS_BYTES = 4;
    private static final int NUM_NODES_BYTES = 4;
    private static final int POSITION_BYTES = 4;

    @Override
    public Leaf<K, V> createLeaf() {
        return new LeafFile<K, V>(options, this, nextLeafPosition());
    }

    private int leafBytes() {
        return relativeLeafKeyPosition(options.maxLeafKeys()) //
                + POSITION_BYTES // next leaf position
        ;
    }

    private int nonLeafBytes() {
        // TODO check this, it looks wrong
        return NUM_NODES_BYTES + (2 * options.maxNonLeafKeys() - 1) * (keySerializer.maxSize() + POSITION_BYTES);
    }

    private long nextLeafPosition() {
        int i = index;
        // shift by max size of a leaf node: numKeys, keys, values, next leaf position
        // (b+tree pointer to next leaf node)
        index += leafBytes();
        return i;
    }

    private int relativeLeafKeyPosition(int i) {
        return NUM_KEYS_BYTES + i * (keySerializer.maxSize() + valueSerializer.maxSize());
    }

    @Override
    public NonLeaf<K, V> createNonLeaf() {
        return new NonLeafFile<K, V>(this, nextNonLeafPosition());
    }

    private long nextNonLeafPosition() {
        int i = index;
        index += nonLeafBytes();
        return i;
    }

    public K leafKey(long position, int i) {
        int p = (int) (position + relativeLeafKeyPosition(i));
        bb.position((int) p);
        return keySerializer.read(bb);
    }

    public int numKeys(long position) {
        return bb.getInt((int) position);
    }

    public V value(long position, int i) {
        int p = (int) (position + NUM_KEYS_BYTES + i * (keySerializer.maxSize() + valueSerializer.maxSize())
                + keySerializer.maxSize());
        bb.position(p);
        return valueSerializer.read(bb);
    }

    public void setNumKeys(long position, int numKeys) {
        bb.position((int) position);
        bb.putInt(numKeys);
    }

    public void setValue(long position, int i, V value) {
        int p = (int) (position + NUM_KEYS_BYTES + i * (keySerializer.maxSize() + valueSerializer.maxSize())
                + keySerializer.maxSize());
        bb.position(p);
        valueSerializer.write(bb, value);
    }

    public void insert(long position, int i, K key, V value) {
        int p = (int) (position + NUM_KEYS_BYTES + i * (keySerializer.maxSize() + valueSerializer.maxSize()));
        bb.position(p);
        keySerializer.write(bb, key);
        bb.position(p + keySerializer.maxSize());
        valueSerializer.write(bb, value);
    }

    public void move(long position, int start, int length, LeafFile<K, V> other) {
        int p = (int) (position + NUM_KEYS_BYTES + start * (keySerializer.maxSize() + valueSerializer.maxSize()));
        byte[] bytes = new byte[length * (keySerializer.maxSize() + valueSerializer.maxSize())];
        bb.position(p);
        bb.get(bytes);
        p = (int) (other.position() + NUM_KEYS_BYTES + start * (keySerializer.maxSize() + valueSerializer.maxSize()));
        bb.position(p);
        bb.put(bytes);
    }

    public void setNext(long position, LeafFile<K, V> sibling) {
        int p = (int) (position + NUM_KEYS_BYTES
                + options.maxLeafKeys() * (keySerializer.maxSize() + valueSerializer.maxSize()));
        bb.putInt(p, (int) sibling.position());
    }

    public Leaf<K, V> next(long position) {
        return new LeafFile<K, V>(options, this, position);
    }

    @Override
    public void close() throws Exception {
        // TODO
    }
}
