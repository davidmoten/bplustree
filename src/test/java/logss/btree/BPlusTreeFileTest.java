package logss.btree;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import logss.btree.internal.file.FactoryFile;
import logss.btree.internal.file.LeafFile;

public class BPlusTreeFileTest {

    private static BPlusTree<Integer, Integer> create(int maxKeys) {
        Serializer<Integer> serializer = new Serializer<Integer>() {

            @Override
            public Integer read(ByteBuffer bb) {
                return bb.getInt();
            }

            @Override
            public void write(ByteBuffer bb, Integer t) {
                bb.putInt(t);
            }

            @Override
            public int maxSize() {
                return Integer.BYTES;
            }
        };

        return BPlusTree.<Integer, Integer>builder().factoryProvider(options -> new FactoryFile<Integer, Integer>( //
                options, //
                new File("target"), //
                serializer, serializer)).maxKeys(maxKeys) //
                .naturalOrder();
    }

    @Test
    public void testInsertOne() {
        BPlusTree<Integer, Integer> tree = create(2);
        tree.insert(1, 10);
        LeafFile<Integer, Integer> leaf = (LeafFile<Integer,Integer>)tree.root();
        System.out.println(leaf.position());
        FactoryFile<Integer,Integer> factory = leaf.factory();
        byte[] bytes = Arrays.copyOf(factory.data(), 100);
        System.out.println(Arrays.toString(bytes));
    }

}
