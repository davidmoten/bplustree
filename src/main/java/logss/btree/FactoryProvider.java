package logss.btree;

public interface FactoryProvider<K, V> {

    Factory<K, V> createFactory(Options<K, V> options);

}
