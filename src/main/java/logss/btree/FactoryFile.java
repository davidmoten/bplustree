package logss.btree;

import java.io.File;

public final class FactoryFile<K, V> implements Factory<K, V> {

    private final Options<K, V> options;
    private final File index;
    private final File data;

    public FactoryFile(Options<K, V> options, File index, File data) {
        this.options = options;
        this.index = index;
        this.data = data;
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

}
