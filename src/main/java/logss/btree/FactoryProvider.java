package logss.btree;

import logss.btree.internal.file.FactoryFile;

public interface FactoryProvider<K, V> {

    Factory<K, V> createFactory(Options<K, V> options);

    public static FactoryFile.Builder file() {
        return FactoryFile.builder();
    }

}
