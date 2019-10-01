package logss.btree;

import logss.btree.internal.Options;

public interface FactoryProvider<K, V> {

    Factory<K, V> createFactory(Options<K, V> options);

}
