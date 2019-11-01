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

    private static final int NUM_KEYS_BYTES = 4;
    private static final int NUM_NODES_BYTES = 4;
    private static final int POSITION_BYTES = 4;

    //////////////////////////////////////////////////
    // Format of a Leaf
    // NUM_KEYS (KEY VALUE)* NEXT_LEAF_POSITION
    // Every Leaf has space allocated for maxLeafKeys key value pairs
    //////////////////////////////////////////////////

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
        // every key has a child node to the left and the final key has a child node to
        // the right as well as the left
        return NUM_NODES_BYTES + options.maxNonLeafKeys() * (keySerializer.maxSize() + POSITION_BYTES) + POSITION_BYTES;
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

    public K leafKey(long position, int i) {
        int p = (int) (position + relativeLeafKeyPosition(i));
        bb.position((int) p);
        return keySerializer.read(bb);
    }

    public int leafNumKeys(long position) {
        return bb.getInt((int) position);
    }

    public V leafValue(long position, int i) {
        int p = (int) (position + NUM_KEYS_BYTES + i * (keySerializer.maxSize() + valueSerializer.maxSize())
                + keySerializer.maxSize());
        bb.position(p);
        return valueSerializer.read(bb);
    }

    public void leafSetNumKeys(long position, int numKeys) {
        bb.position((int) position);
        bb.putInt(numKeys);
    }

    public void leafSetValue(long position, int i, V value) {
        int p = (int) (position + NUM_KEYS_BYTES + i * (keySerializer.maxSize() + valueSerializer.maxSize())
                + keySerializer.maxSize());
        bb.position(p);
        valueSerializer.write(bb, value);
    }

    public void leafInsert(long position, int i, K key, V value) {
        int p = (int) (position + NUM_KEYS_BYTES + i * (keySerializer.maxSize() + valueSerializer.maxSize()));
        bb.position(p);
        keySerializer.write(bb, key);
        bb.position(p + keySerializer.maxSize());
        valueSerializer.write(bb, value);
        // increment number of keys in leaf node
        bb.position((int) position);
        bb.putInt(leafNumKeys(position) + 1);
    }

    public void leafMove(long position, int start, int length, LeafFile<K, V> other) {
        int p = (int) (position + NUM_KEYS_BYTES + start * (keySerializer.maxSize() + valueSerializer.maxSize()));
        byte[] bytes = new byte[length * (keySerializer.maxSize() + valueSerializer.maxSize())];
        bb.position(p);
        bb.get(bytes);
        p = (int) (other.position() + NUM_KEYS_BYTES + start * (keySerializer.maxSize() + valueSerializer.maxSize()));
        bb.position(p);
        bb.put(bytes);
        // set the number of keys in source node to be `start`
        bb.position((int) position);
        bb.putInt(start);

        bb.position((int) other.position());
        bb.putInt(length);
    }

    public void leafSetNext(long position, LeafFile<K, V> sibling) {
        int p = (int) (position + NUM_KEYS_BYTES
                + options.maxLeafKeys() * (keySerializer.maxSize() + valueSerializer.maxSize()));
        bb.putInt(p, (int) sibling.position());
    }

    public Leaf<K, V> leafNext(long position) {
        return new LeafFile<K, V>(options, this, position);
    }

    //////////////////////////////////////////////////
    // Format of a NonLeaf
    // NUM_KEYS (KEY LEFT_CHILD_POSITION)* RIGHT_CHILD_POSITION
    // Every NonLeaf has space allocated for maxNonLeafKeys keys
    //////////////////////////////////////////////////

    
    @Override
    public NonLeaf<K, V> createNonLeaf() {
        return new NonLeafFile<K, V>(options, this, nextNonLeafPosition());
    }

    private long nextNonLeafPosition() {
        int i = index;
        index += nonLeafBytes();
        return i;
    }


    public void nonLeafSetNumKeys(long position, int numKeys) {
        bb.position((int) position);
        bb.putInt(numKeys);
    }

    public int nonLeafNumKeys(long position) {
        return bb.getInt((int) position);
    }

    public void nonLeafSetChild(long position, int i, NodeFile node) {
        //TODO
    }

    public Node<K, V> nonLeafChild(long position, int i) {
        // TODO Auto-generated method stub
        return null;
    }

    public K nonLeafKey(long position, int i) {
        // TODO Auto-generated method stub
        return null;
    }

    public void nonLeafSetKey(long position, int i, K key) {
        // TODO Auto-generated method stub

    }

    public void nonLeafMove(long position, int mid, int length, NonLeafFile<K, V> other) {
        // TODO Auto-generated method stub

    }

    public void nonLeafInsert(long position, K key, Node<K, V> left) {
        // TODO Auto-generated method stub

    }
    
    @Override
    public void close() throws Exception {
        // TODO
    }

}
