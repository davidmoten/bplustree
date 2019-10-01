package logss.btree.internal.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;

import logss.btree.Factory;
import logss.btree.Leaf;
import logss.btree.NonLeaf;
import logss.btree.Options;

public final class FactoryFile<K, V> implements Factory<K, V> {

    private final Options<K, V> options;
    private final RandomAccessFile index;
    private final File indexFile;
    private final File dataFile;

    public FactoryFile(Options<K, V> options, File indexFile, File dataFile) {
        this.options = options;
        this.indexFile = indexFile;
        this.dataFile = dataFile;
        try {
            this.index= new RandomAccessFile(indexFile, "rw");
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Leaf<K, V> createLeaf() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NonLeaf<K, V> createNonLeaf() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() throws Exception {
        index.close();
    }

}
